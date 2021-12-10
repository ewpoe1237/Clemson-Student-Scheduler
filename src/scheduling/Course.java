package scheduling;

import java.util.ArrayList;
import java.util.HashMap;

/*
    The Course class represents a Course object, used in Scheduler to represent courses
    that the student has taken or may need.
 */
public class Course {
    private String courseCode, description, attr, coreqList, requiredList, optionalList, type;
    private int creditHours;

    //may not need hasPlacementScore... check later
    //also questioning hasRequirements so "
    private boolean hasRequired, hasOptional;

    private HashMap<String, Integer> inputCodes;

    public Course() {
        this.courseCode = "NULL";
        this.description = "NULL";
        //describes the type of requirement the course fulfills
        this.type = "NULL";
        //attr is just the list of gen ed attributes the course may have
        this.attr = "NULL";

        this.coreqList = "NULL";
        this.requiredList = "NULL";
        this.optionalList = "NULL";
        this.creditHours = -1;

        //initialize has optional/required to false, as we do not know if the student has completed these yet
        hasRequired = false;
        hasOptional = false;
    }
    public Course(String courseCode,
                  String description,
                  String type,
                  String attr,
                  String coreqList,
                  String requiredList,
                  String optionalList,
                  int creditHours) {
        this.courseCode = courseCode;
        this.description = description;
        //describes the type of requirement the course fulfills
        this.type = type;
        //attr is just the list of gen ed attributes the course may have
        this.attr = attr;

        this.coreqList = coreqList;
        this.requiredList = requiredList;
        this.optionalList = optionalList;
        this.creditHours = creditHours;

        //initialize has optional/required to false, as we do not know if the student has completed these yet
        hasRequired = false;
        hasOptional = false;
    }

    public void setInputCodes(HashMap<String, Integer> inputCodes) {
        this.inputCodes = inputCodes;
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

    public void setOptional(String optionalList) {
        this.optionalList = optionalList;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getAttr() {
        return attr;
    }

    public String getCoreqList() {
        return coreqList;
    }

    public String getRequiredList() {
        return requiredList;
    }

    public String getOptionalList() {
        return optionalList;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public boolean hasAllRequirements(HashMap<String, Integer> inputCodes) {
        hasOptional = processOptional(inputCodes);
        hasRequired = processRequired(inputCodes);

        return (hasOptional && hasRequired);
    }

    private boolean processRequired(HashMap<String, Integer> inputCodes) {
        //Separate individual (all required) prereqs, coreqs, and attributes with hyphens- E.G 'CPSC 1060-CPSC 1070'

        //edge case: we don't have required preqs. then we can just return true:
        if(requiredList.length() == 0) return true;

        if(requiredList.charAt(0) == '!') requiredList = requiredList.substring(1);
        if(requiredList.length() != 0) {
            if(requiredList.charAt(requiredList.length() - 1) == '!') requiredList = requiredList.substring(0, requiredList.length() - 1);
        }

        String[] separatedRequirements = requiredList.split("-");

        for(int i = 0; i < separatedRequirements.length; i++) {
            if(separatedRequirements[i].trim().length() == 0) continue; //takes care of any extra hyphens by accounting for "blank requirements"

            //iterate thru split requirements to make sure every single one is fulfilled. if one hasn't been fulfilled return false since everything collectively is required
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

        if(optionalList.charAt(0) == '!') optionalList = optionalList.substring(1);

        if(optionalList.length() != 0) {
            if(optionalList.charAt(optionalList.length() - 1) == '!') optionalList = optionalList.substring(0, optionalList.length() - 1);
        }

        String[] separatedGroups = optionalList.split("!");
        int fulfilledCounter = 0, amountOfGroups = 0;

        for(int i = 0; i < separatedGroups.length; i++) {
            if(separatedGroups[i].trim().length() == 0) continue; //extra check for extra exclamation marks

            //other than empty portions we want to keep track of every group fulfilled so we need to count the amt of groups for that course
            amountOfGroups++;

            String[] separatedRequirements = separatedGroups[i].split("-"); //splits each group up further into every optional class
            for(int j = 0; j < separatedRequirements.length; j++) { //iterate through every individual class in each group
                if(separatedRequirements[j].trim().length() == 0) continue; //error handling

                if(inputCodes.containsKey(separatedRequirements[j])) {
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
        if(coreqList.length() == 0) return null;

        if(coreqList.charAt(0) == '!') coreqList = coreqList.substring(1);
        if(coreqList.length() != 0) {
            if (coreqList.charAt(coreqList.length() - 1) == '!')
                coreqList = coreqList.substring(0, coreqList.length() - 1);
        }

        String[] coreqCodes = coreqList.split("-");
        ArrayList<String> processedCodes = new ArrayList<>();

        for(int i = 0; i < coreqCodes.length; i++) {
            if(coreqCodes[i].trim().length() == 0) continue;
            else if(inputCodes.containsKey(coreqCodes[i])) continue; //if the student has already taken the coreq they will not need that coreq

            processedCodes.add(coreqCodes[i]); //if none of those if statements went through, we are good to add to processed codes
        }

        return processedCodes;
    }

    //returns the attributes this course has in an arraylist for the scheduler to use
    public ArrayList<String> getProcessedAttributes() {
        if(attr.length() == 0) return null;

        String[] attrCodes = attr.split("-");
        ArrayList<String> processedCodes = new ArrayList<>();

        for(int i = 0; i < attrCodes.length; i++) {
            if(attrCodes[i].length() == 0) continue;

            processedCodes.add(attrCodes[i]);
        }

        return processedCodes;
    }
}
