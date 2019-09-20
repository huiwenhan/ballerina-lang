// This example imports a module. You can only refer to the public symbols of
// an imported module.
import ballerina/math;

// Declare an explicit alias.
import ballerina/io as console;

public function main() {

    // Refer symbols of another module.
    // `math:PI` is a qualified identifier. Note the usage of the module alias.
    float piValue = math:PI;

    // Use the explicit alias `console` to invoke a function defined in the `ballerina/io` module.
    console:println(piValue);
}
