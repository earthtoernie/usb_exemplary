# Do all the usb things (with a gui too!)

### gradle commands

`./gradlew run`

https://docs.gradle.org/current/samples/sample_java_modules_with_transform.html  

`./gradlew run`

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