// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

function testFreezeOnNilTypedValue() {
    () n = ();
    _ = n.freeze();
}

function testFreezeOnValuesOfNonAnydataType() {
    PersonObj p = new;
    PersonObj q = p.freeze();

    stream<int> intSt;
    _ = intSt.freeze();

    future<boolean> boolFuture;
    _ = boolFuture.freeze();
}

function testFreezeOnMapWithoutAnydata() {
    map<PersonObj> m1;
    _ = m1.freeze();

    map<stream|PersonObj> m2;
    _ = m2.freeze();
}

function testFreezeOnArrayWithoutAnydata() {
    PersonObj[] a1;
    _ = a1.freeze();

    (PersonObjTwo|PersonObj)[] a2;
    _ = a2.freeze();
}

function testFreezeOnTupleWithoutAnydata() {
    (PersonObj|PersonObjTwo, PersonObjTwo) t1;
    _ = t1.freeze();
}

function testFreezeOnRecordWithoutAnydata() {
    Department d1;
    _ = d1.freeze();
}

function testIsFrozenOnBasicTypes() {
    int i = 5;
    boolean b = i.isFrozen();

    byte bt = 255;
    b = bt.isFrozen();

    float f = 12.5;
    b = f.isFrozen();

    string s = "Hello from Ballerina";
    b = s.isFrozen();

    boolean bl = false;
    b = bl.isFrozen();
}

function testInvalidAssignmentWithFreeze() {
    map<string|PersonObj> m;
    map<string|PersonObj> m1 = m.freeze();

    map<(string|PersonObj, FreezeAllowedDepartment|float)> m2;
    map<(any, any)> m3 = m2.freeze();

    (boolean|PersonObj|float)[] a1;
    (boolean|PersonObj|float)[] a2 = a1.freeze();

    any[] a3 = a1.freeze();

    (string|PersonObj, FreezeAllowedDepartment|float) t1;
    (string|PersonObj, FreezeAllowedDepartment|float) t2 = t1.freeze();

    FreezeAllowedDepartment fd;
    FreezeAllowedDepartment fd2 = fd.freeze();
}

function testFreezeAndIsFrozenOnUnionType() {
    string|int u = "hi";
    _ = u.freeze();
    _ = u.isFrozen();

    string|PersonObj u2 = "hi";
    _ = u2.freeze();
    _ = u2.isFrozen();
}

type PersonObj object {
    string name;

    function getName() returns string {
        return self.name;
    }
};

type PersonObjTwo object {
    string id;

    function getId() returns string {
        return self.id;
    }
};

type Department record {
    PersonObj head;
    PersonObjTwo...
};

type FreezeAllowedDepartment record {
    PersonObj|string head;
    (PersonObjTwo|string)...
};
