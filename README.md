#ParcelablePlease
An AnnotationProcessor for generating Android Parcelable boilerplate code. [See this blog entry](http://hannesdorfmann.com/android/ParcelablePlease/) for comparison with other parcel libraries.

#Dependency
Latest version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.hannesdorfmann.parcelableplease/annotation/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.hannesdorfmann.parcelableplease/annotation)

```groovy
compile 'com.hannesdorfmann.parcelableplease:annotation:x.x.x'
apt 'com.hannesdorfmann.parcelableplease:processor:x.x.x'
```
In android studio you need to apply Hugo Visser's awesome [android-apt](https://bitbucket.org/hvisser/android-apt) gradle plugin to enable annotation processing.

#How to use
Simply annotate the classes you want to make Parcelable with `@ParcelablePlease` and implement the `Parcelable` as well as the `CREATOR` (This step can be automated by using the Android Studio plugin, see below).

```java
@ParcelablePlease
public class Model implements Parcelable {

  int id;
  String name;
  OtherModel otherModel;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    ModelParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<Model> CREATOR = new Creator<Model>() {
    public Model createFromParcel(Parcel source) {
      Model target = new Model();
      ModelParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public Model[] newArray(int size) {
      return new Model[size];
    }
  };
}

```
The `ParcelablePlease` annotation processor will generate a class named `ClassName + ParcelablePlease` for you with all the code for writing and reading data in the Parcel.  So from the example above: `ModelParcelablePlease` is generated and provides two static methods: `ModelParcelablePlease.readFromParcel(Model, Parcel)` and `ModelParcelablePlease.writeToParcel(Model, Parcel, int)`

Once you have done this basic setup by connecting the generated code with your Model class you can change the model class, add fields, remove fields etc. without worring about `Parcelable` because `ParcelablePlease` will generate the code for you everytime you compile.

# Android Studio Plugin
Like mentioned above you have to write few lines of code to connect the Parcelable class with the generated code. Don't worry, you don't have to do this by hand. There is a Android Studio / IntelliJ plugin that can do that for you:

 1. Download the plugin from [here](https://github.com/sockeqwe/ParcelablePlease/raw/master/ParcelablePlease-intellij-plugin/ParcelablePlease-intellij-plugin.jar)
 2. Open Android Studio / IntelliJ 
 3. Open the Preferences (on Mac with `⌘ + ;` )
 4. Type in the searchbox "plugin" to navigate quickly to the plugins section
 5. Click on the `Intall Plugin From Disk` Button and select the downloaded plugin .jar file
 
 ![Preferences](https://github.com/sockeqwe/ParcelablePlease/raw/master/ParcelablePlease-intellij-plugin/images/intellij-plugin.png "Preferences")

 6. Restart Android Studio
 7. Create a Model class and open the Generate Menu (on Mac with  ``⌘ + n` ). Note that the cursor must be somewhere in the code of the class.
 
  ![Preferences](https://github.com/sockeqwe/ParcelablePlease/raw/master/ParcelablePlease-intellij-plugin/images/generate.png "Preferences")


 Remember that you may have to compile your project to make Android Studio run annotation Processing which will generate the ParcelPlease classes.


# Supported types

 - **Primitives**
    - byte
    - boolean
    - double
    - float
    - int
    - long
    - String
    
 - **Primitive wrappers**
     - Byte
     - Boolean
     - Double
     - Float
     - Int
     - Long
     
 - **Android specific**
     - Parcelable (anything that implements Parcelable)
     - Bundle
     - SparseBooleanArray
 
 - **Arrays**
     - int[]
     - long[]
     - double[]
     - String[]
     - float[]
     - char[]
     - boolean[]
     - byte[]
     - Parcelable[] - array of anything that implements Parcelable
 
 - **Other**
    - Serializable
    - java.util.Date (by simpling passing time as millis) 
 
 
 - **Collections**
     - List<? extends Parcelalble>
     - ArrayList<? extends Parcelable>
     - LinkedList<? extends Parcelable>
     - CopyOnWriteArrayList<? extends Parcelable>
     
     
# Bagger
Do you want to make a field Parcelable but it's not listed in the supported types list from above (i.e. `java.util.Map`)? No Problem: You can provide your own implementation implementing a `ParcelBagger` like this:
```java
public class DateBagger implements ParcelBagger<Date> {

  @Override public void write(Date value, Parcel out, int flags) {
    if (value == null) {
      out.writeLong(-1);
    } else {
      out.writeLong(value.getTime());
    }
  }

  @Override public Date read(Parcel in) {

    long timeMillis = in.readLong();
    if (timeMillis == -1) {
      return null;
    }

    return new Date(timeMillis);
  }
}
```

You can use your ParcelBagger with the `@Bagger` annotation like this:

```java
@ParcelablePlease
public class Person implements Parcelable {

  int id;
  String name;
  
  @Bagger(DateBagger.class)
  Date date;
  
}
```

Remember that you have to take care about special cases like what if the value is null.
**Note** that `java.util.Date` is already supported by `ParcelablePlease`. The example above is just to give you an idea of how a implementation could look like.


# Configuration
You can configure which fields should be serialized. There are two ways:
 1. As default all class (and super classes) fields will be serialized. You can mark field's you don't want to serialize by annotating them with `@ParcelableNoThanks`
 2. You can do the other way: You could change the settings to only serialize fields that are marked with `@ParcelableThisPlease ` like this:
 ```java
 @ParcelablePlease( allFields = false)
 public class Animal implements Parcelable {
    
    @ParcelableThisPlease
    String name;
    
    int age; // This will not be serialized 
    
 }
 ```
 

As default `ParcelablePlease` will throw a compile error if it tries to serialize private fields (private fields are not supported because of visibility issues). If your class marked with `@ParcelablePlease` contains private fields you could mark them as not parcelable with `@ParcelableNoThanks` or you could cofigure ParcelablePlease to skip private fields by using `@ParcelablePlease( ignorePrivateFields = true)`:
 
 ```java
 @ParcelablePlease( ignorePrivateFields = true)
 public class Person implements Parcelable {
    
    String name;
    
    private int age; // No compile error 
    
 }
 ```
     
# Limitations
 - **Fields** must have at least default (package) visibility. That means private fields are not supported.
 - **Private classes** are not supported because of visibilitiy issues
