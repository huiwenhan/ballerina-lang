/*
 * Copyright (c) 2019, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver.codeaction.providers;

import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.projects.Package;
import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.langserver.common.CommonKeys;
import org.ballerinalang.langserver.common.constants.CommandConstants;
import org.ballerinalang.langserver.common.utils.CommonUtil;
import org.ballerinalang.langserver.commons.CodeActionContext;
import org.ballerinalang.langserver.compiler.LSPackageLoader;
import org.ballerinalang.langserver.completions.util.ItemResolverConstants;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentEdit;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Code Action for importing a module.
 *
 * @since 1.2.0
 */
@JavaSPIService("org.ballerinalang.langserver.commons.codeaction.spi.LSCodeActionProvider")
public class ImportModuleCodeAction extends AbstractCodeActionProvider {
    private static final String UNDEFINED_MODULE = "undefined module";

    @Override
    public List<CodeAction> getDiagBasedCodeActions(Diagnostic diagnostic, CodeActionContext context) {
        List<CodeAction> actions = new ArrayList<>();
        if (!(diagnostic.getMessage().startsWith(UNDEFINED_MODULE))) {
            return actions;
        }
        String uri = context.fileUri();
        List<Diagnostic> diagnostics = new ArrayList<>();
        String diagnosticMessage = diagnostic.getMessage();
        String packageAlias = diagnosticMessage.substring(diagnosticMessage.indexOf("'") + 1,
                diagnosticMessage.lastIndexOf("'"));
        List<Package> packagesList = new ArrayList<>(LSPackageLoader.getDistributionRepoPackages());

        packagesList.stream()
                .filter(pkgEntry -> {
                    String pkgName = pkgEntry.packageName().value();
                    return pkgName.endsWith("." + packageAlias) || pkgName.endsWith(packageAlias);
                })
                .forEach(pkgEntry -> {
                    String pkgName = pkgEntry.packageName().value();
                    String commandTitle = String.format(CommandConstants.IMPORT_MODULE_TITLE, pkgName);
                    String moduleName = CommonUtil.escapeModuleName(context, pkgName);
                    CodeAction action = new CodeAction(commandTitle);
                    Position insertPos = getImportPosition(context);
                    String importText = ItemResolverConstants.IMPORT + " " + pkgEntry.packageOrg().value() + "/"
                            + moduleName + CommonKeys.SEMI_COLON_SYMBOL_KEY + CommonUtil.LINE_SEPARATOR;
                    List<TextEdit> edits = Collections.singletonList(
                            new TextEdit(new Range(insertPos, insertPos), importText));
                    action.setKind(CodeActionKind.QuickFix);
                    action.setEdit(new WorkspaceEdit(Collections.singletonList(Either.forLeft(
                            new TextDocumentEdit(new VersionedTextDocumentIdentifier(uri, null), edits)))));
                    action.setDiagnostics(diagnostics);
                    actions.add(action);
                });
        return actions;
    }

    private static Position getImportPosition(CodeActionContext context) {
        // Calculate initial import insertion line
        Optional<SyntaxTree> syntaxTree = context.workspace().syntaxTree(context.filePath());
        ModulePartNode modulePartNode = syntaxTree.orElseThrow().rootNode();
        NodeList<ImportDeclarationNode> imports = modulePartNode.imports();
        if (imports.isEmpty()) {
            return new Position(0, 0);
        }
        ImportDeclarationNode lastImport = imports.get(imports.size() - 1);
        return new Position(lastImport.lineRange().endLine().line() + 1, 0);
    }
}
