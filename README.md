Work in progress

----------------


# Supported types:

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
 
 - **Arrays**
     - int[]
     - long[]
     - double[]
     - String[]
     - float[]
     - char[]
     - boolean[]
     - byte[]
     - Parcelable[] (array of anything that implements Parcelable)
 
 - **Other**
    - Serializable
    - java.util.Date (by simpling passing time as millis) 
 
 
 - **Collections**
     - List<? extends Parcelalble>
     - ArrayList<? extends Parcelable>
     - LinkedList<? extends Parcelable>
     - CopyOnWriteArrayList<? extends Parcelable>