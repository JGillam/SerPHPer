# SerPHPer
Serialized PHP toolkit for Burp Suite

From a security testing perspective, one interesting feature of PHP is that of PHP Serialized objects. They typically show up as Base64 encoded strings which, once decoded, resemble a format that looks something like JSON (only not).  See [PHP Internals on Serialization](http://www.phpinternalsbook.com/classes_objects/serialization.html) for more detais on the format.

Issues presented by PHP serialization when these objects are passed from the browser and deserialized on the server include:
   * In some circumstances, deserialization of these objects may result in some level of control such as writing files or remote execution.
   * PHP serialized values are usually base64 encoded, thereby ignored by web application firewall (WAF) rules.
   * Developers often forget to perform input validation and output encoding on the contents of serialized objects.
   * Most security testing tools don't know how to handle parameters embedded inside serialized objects, which means all of the above are that much more dangerous.
   
The purpose of the SerPHPer toolkit, which is a [Burp Suite](https://portswigger.net/) extension, is to facilitate security testing of PHP serialized objects.

## Limitations
The current functionality should be considered an "alpha" release. The only thing working so far is a transformation from PHP Serialized to JSON(-ish) and back, and even that is limited to only strings, ints, and arrays.  There is much more to come.

The "JSON-ish" format is not a perfect representation of JSON. It is just meant to be a simpler markup to make manual editing of PHP serialized data much easier.
