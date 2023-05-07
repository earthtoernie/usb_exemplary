# Do all the usb things (with a gui too!). Fun with GRADLE :)

- Started on a project to do a bunch of things with very custom USB
devices, in Java. Experimented with JavaFX, which I really like working with.

- Uses [usb4java](https://github.com/usb4java/usb4java) which was very much NOT compatible
with the java module system. Used some gradle magic to shoehorn everything to use
modern modules!

- Learned a ton about java packaging.

- Most of the work thus far has been "hacking" with gradle and taking it to its knees!
Hope to complete this one day, but if anybody wants to get some old libs to work
with Gradele, come check this one out! Specifically "native" JNI libraries was added to modules,
direct from [maven central](https://mvnrepository.com/artifact/org.usb4java/libusb4java/1.3.0).
Tested to work on linux, Raspberry PI, and windows, which is pretty cool, as its a universal!

- Also added some hooks to build a local [sqlite](sqlite.org) database from text, to build a database from a text source
an [online source](http://www.linux-usb.org/usb.ids"), also kinda a cool Gradle trick.

### gradle commands

`./gradlew run`

https://docs.gradle.org/current/samples/sample_java_modules_with_transform.html  

`./gradlew run`

## A bunch of notes for myself (could use some cleaning up)
"java module export a resource"
https://leward.eu/2020/02/16/java-load-resource-from-another-module.html
ModuleLayer.boot().modules().stream().filter(x -> x.getName().contains("usb")).collect(Collectors.<Module>toList())
ModuleLayer.boot().modules().stream().filter(x -> x.getName().contains("org.usb4java-libusb4java-linux-x86-64")).collect(Collectors.<Module>toList()).get(0)
ModuleLayer.boot().modules().stream().filter(x -> x.getName().contains("org.usb4java-libusb4java-linux-x86-64")).collect(Collectors.<Module>toList()).get(0).getResourceAsStream("org/usb4java/linux-x86-64/libusb4java.so")

https://leward.eu/2020/02/16/java-load-resource-from-another-module.html

org.usb4javaNative.Dummy.class.getResource(source);

java modules use classloader to get resource from another module

https://stackoverflow.com/questions/46861589/accessing-resource-files-from-external-modules // good info!

ClassLoader.getSystemResources("libusb4java.so")

ClassLoader.getSystemResources("libusb4java.so")
Loader.class.getResource("/org/usb4java/Loader.class")
Loader.class.getResource("/com/earthtoernie/bin/Empty.class")
https://leward.eu/2020/02/16/java-load-resource-from-another-module.html
ModuleLayer.boot().modules()

Empty.class.getResource("libusb4java.so")

Empty.class.getModule().isExported("Empty")
https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/Module.html

----------------------
https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/Module.html#isExported(java.lang.String)
Empty.class.getModule().isOpen("com.earthtoernie.bin")
org.usb4java.Loader.class.getModule() --> usb4java.JNI

Empty.class.getModule().isOpen("com.earthtoernie.bin", org.usb4java.Loader.class.getModule()) --> true
Empty.class.getResource("/org/usb4java/linux-x86-64/libusb4java.so") -> works!
ClassLoader.getSystemResource("org/usb4java/linux_x86_64/libusb4java.so")

`SUBSYSTEMS=="usb", ATTR{idVendor}=="08f7", ATTR{idProduct}=="0002", GROUP="dialout", MODE="664"`
`/etc/udev/rules.d/90-myusb.rules`

how to reset git repo like it was just checked out
`find . -path ./.git -prune -o -exec rm -rf {} \; 2> /dev/null`