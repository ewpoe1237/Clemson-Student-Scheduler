# Clemson CS Student Scheduler
<h2>Jenna Hofseth, Fall 2021-<i>present?</i></h2>
<h3>Java with Swing, AWS (RDS & EC2), MySQL</h3>

(DB CURRENTLY DOWN; MYSQL QUERY FILE IN REPO)<br>
Scheduling GUI program that uses user input (i.e. max credits per semester, semesters left, classes already taken..) and walks the user through creating a semi-automatic semester-by-semester schedule.<br>

<h2>Admin Controls</h2>
If the user is an admin, they can freely add classes and update any already in the database, as well as view all classes according to category. 
- Classes have several different options for categories, corresponding to types of classes that need to be fulfilled before graduation. For example, CPSC 1060 and 1070 fall under Intro to Computer Science, whereas GEOL 1010 and 1020 fall under Natural Science.<br>
- Classes also have required course code, title, and credit hour attributes, as well as optional corequisite, required prerequisite, and group prerequisites attributes that are input in a format that the program can then use to parse and algorithmically form a schedule.<br>
- "Required" prerequisites are those that are collectively required classes (for example, if a student were required to take Algebra 1 and 2 before Precalculus, they would be considered required). If they are within groups of classes where only one of each group is needed, the prerequisite is considered a "group" prerequisite (for example, if a student were required to take one of CPSC 1060 or 1070, and also one of either CPSC 1010 or 1020, these would be considered groups).<br>

  <h2>Student Controls</h2>
- If the user is a student, they will automatically be brought to a menu where they can select the amount of semesters they have left, maximum credits per semester, and whether they want the completed schedule exported to a .txt.<br>
- After this, the student can select each course they have already taken, with the list of course codes being those read in from the database, by control-clicking.<br>
- The user is then taken through each semester where the courses are algorithmically generated according to category and user input. The student can experiment by changing classes using the dropdown to the right of each category. When they are ready to continue, they can move on and the next semester's schedule will be generated.<br>
