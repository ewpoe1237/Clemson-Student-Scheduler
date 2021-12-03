package scheduling;

import java.util.ArrayList;
import java.util.HashMap;

/*
    The Course class represents a Course object, used in Scheduler to represent courses
    that the student has taken or may need.
 */
public class Course {
    private String courseCode, description, attr, coreqList, requiredList, optionalList;
    private int creditHours;

    //may not need hasPlacementScore... check later
    //also questioning hasRequirements so "
    private boolean hasRequired, hasOptional;

    private HashMap<String, Integer> inputCodes;

    public Course(HashMap<String, Integer> inputCodes,
                  String courseCode,
                  String description,
                  String attr,
                  String coreqList,
                  String requiredList,
                  String optionalList,
                  int creditHours) {
        this.inputCodes = inputCodes;
        this.courseCode = courseCode;
        this.description = description;

        //genEds is just the list of gen ed attributes the course may have
        this.attr = attr;

        this.coreqList = coreqList;
        this.requiredList = requiredList;
        this.optionalList = optionalList;
        this.creditHours = creditHours;

        //initialize has optional/required to false, as we do not know if the student has completed these yet
        hasRequired = false;
        hasOptional = false;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public void setCoreqList(String coreqList) {
        this.coreqList = coreqList;
    }

    public void setRequired(String requiredList) {
        this.requiredList = requiredList;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getDescription() {
        return description;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public boolean hasAllRequirements() {
        hasOptional = processOptional(inputCodes);
        hasRequired = processRequired(inputCodes);

        return (hasOptional && hasRequired);
    }

    private boolean processRequired(HashMap<String, Integer> inputCodes) {
        //Separate individual (all required) prereqs, coreqs, and attributes with hyphens- E.G 'CPSC 1060-CPSC 1070'

        //edge case: we don't have required preqs. then we can just return true:
        if(requiredList.length() == 0) return true;

        String[] separatedRequirements = requiredList.split("-");

        for(int i = 0; i < separatedRequirements.length; i++) {
            if(separatedRequirements[i].length() == 0) continue; //takes care of any extra hyphens

            //iterate thru split requirements to make sure every single one is fulfilled. if one hasn't been fulfilled return false
            if(!inputCodes.containsKey(separatedRequirements[i])) {
                return false;
            }
        }

        //if false has not yet been returned we know everything is good, and we can return true :)
        return true;
    }

    private boolean processOptional(HashMap<String, Integer> inputCodes) {
        //Separate group prerequisites with exclamation marks-E.G. '!MATH 1070-MATH 1080!CPSC 1020-CPSC 1010!'

        //edge case: we don't have optional preqs. then we can just return true:
        if(optionalList.length() == 0) return true;

        String[] separatedGroups = optionalList.split("!");
        int fulfilledCounter = 0, amountOfGroups = 0;

        for(int i = 0; i < separatedGroups.length; i++) {
            if(separatedGroups[i].length() == 0) continue; //takes care of any extra exclamation marks

            //other than empty portions we want to keep track of every group fulfilled so we need to count the amt of groups for that course
            amountOfGroups++;

            String[] separatedRequirements = separatedGroups[i].split("-"); //splits each group up further into every optional class
            for(int j = 0; j < separatedRequirements.length; j++) { //iterate through every individual class in each group
                if(separatedRequirements[i].length() == 0) continue; //error handling

                if(inputCodes.containsKey(separatedRequirements[i])) {
                    //if we find any that fulfill according to inputCodes, we can increase fulfilled counter and break into the next group
                    fulfilledCounter++;
                    break;
                }
            }
        }

        if(fulfilledCounter == amountOfGroups) {
            //if we have the same amount of fulfilled groups as our total amount we know the student has met requirements
            return true;
        }

        //otherwise, we know the student has not met requirements--thus return false
        return false;
    }

    //returns an arraylist of corequisites for scheduler to use
    //only returns those the student has not yet taken
    public ArrayList<String> getProcessedCoreqs(HashMap<String, Integer> inputCodes) {
        String[] coreqCodes = coreqList.split("-");
        ArrayList<String> processedCodes = new ArrayList<>();

        for(int i = 0; i < coreqCodes.length; i++) {
            if(coreqCodes[i].length() == 0) continue;
            else if(inputCodes.containsKey(coreqCodes[i])) continue; //if the student has already taken the coreq they will not need that coreq

            processedCodes.add(coreqCodes[i]); //if none of those if statements went through, we are good to add to processed codes
        }

        return processedCodes;
    }

    //returns the attributes this course has in an arraylist for the scheduler to use
    public ArrayList<String> getProcessedAttributes() {
        String[] attrCodes = attr.split("-");
        ArrayList<String> processedCodes = new ArrayList<>();

        for(int i = 0; i < attrCodes.length; i++) {
            if(attrCodes[i].length() == 0) continue;

            processedCodes.add(attrCodes[i]);
        }

        return processedCodes;
    }
}
