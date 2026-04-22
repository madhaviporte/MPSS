# MPSS - Motor Part Shop Software

## Description

MPSS ek Java Swing based desktop application hai jo motor parts shop ke liye banaya gaya hai. Iska main purpose inventory manage karna, sales record karna, vendors handle karna aur low stock hone par automatic order generate karna hai. Ye system Just-In-Time (JIT) concept follow karta hai jisse unnecessary stock store nahi karna padta.

## Features

* New motor parts add kar sakte hain
* Sales record kar sakte hain
* Stock status automatically show hota hai (IN_STOCK, LOW_STOCK, OUT_OF_STOCK)
* Low stock hone par automatic order suggestion
* Vendor details manage kar sakte hain
* Daily revenue calculate hota hai
* Monthly sales graph show hota hai

## Technologies Used

* Java (Swing)
* OOP concepts

## Project Structure

MPSS/

* src/

  * model/
  * ui/
* bin/ (compiled files - ignored)
* .gitignore
* run.bat

## How to Run

Step 1: Compile
javac -d bin src\model*.java src\ui*.java

Step 2: Run
java -cp bin ui.MainFrame

## Concept Used

Is project me Just-In-Time (JIT) inventory concept use kiya gaya hai jisme stock minimum rakha jata hai aur jab stock threshold se neeche jata hai tab system order suggest karta hai.

