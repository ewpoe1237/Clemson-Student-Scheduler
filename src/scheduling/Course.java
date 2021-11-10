package scheduling;

import java.util.HashMap;

/*
    The Course class represents a Course object, used in Scheduler to represent courses
    that the student has taken or may need.
 */
public class Course {
    private String courseCode, description, genEds, coreqList, prereqList;
    private int creditHours, placementScore;

    //may not need hasPlacementScore... check later
    //also questioning hasRequirements so "
    private boolean hasCoreqs, hasPrereqs, hasCreditHours;
    private boolean hasPlacementScore, hasRequirements;

    private HashMap<String, Integer> inputCodes;

    public Course(HashMap<String, Integer> inputCodes,
                  String courseCode,
                  String description,
                  String genEds,
                  String coreqList,
                  String prereqList,
                  int creditHours) {
        this.inputCodes = inputCodes;
        this.courseCode = courseCode;
        this.description = description;

        //genEds is just the list of gen ed attributes the course may have
        this.genEds = genEds;

        this.coreqList = coreqList;
        this.prereqList = prereqList;
        this.creditHours = creditHours;

        //since the object was created without including a placement score
        hasPlacementScore = false;
        //initializing to false, but instead of doing that make another method in here that checks :)
        hasRequirements = false;
    }

    public Course(HashMap<String, Integer> inputCodes, String courseCode, String description, String genEds, String coreqList, String prereqList, int creditHours, int placementScore) {
        this.inputCodes = inputCodes;
        this.courseCode = courseCode;
        this.description = description;
        this.genEds = genEds;
        this.coreqList = coreqList;
        this.prereqList = prereqList;
        this.creditHours = creditHours;
        this.placementScore = placementScore;

        //since the object was created including a placement score
        hasPlacementScore = true;
        //initializing to false, but instead of doing that make another method in here that checks :)
        hasRequirements = false;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGenEds(String genEds) {
        this.genEds = genEds;
    }

    public void setCoreqList(String coreqList) {
        this.coreqList = coreqList;
    }

    public void setPrereqList(String prereqList) {
        this.prereqList = prereqList;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public void setPlacementScore(int placementScore) {
        this.placementScore = placementScore;

        //in the object had not originally set a placementScore...
        this.hasPlacementScore = true;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getDescription() {
        return description;
    }

    private Boolean processPrerequisites(HashMap<String, Integer> inputCodes) {
        /*
            String of concatenated prerequisites:
            --
            : used to separate codes that are required as prerequisites
            Example -> CPSC 1060:CPSC 1070 means both CPSC 1060 and 1070 are required

            ! used to !break apart groups! of course codes that are among a group where
            only one of those courses is required as a prerequisite.
                Options are surrounded by -
            Example -> !-MATH 1070-MATH 1080-! means either math 1070 OR math 1080 is necessary
            as a prerequisite
                Could just export string into Java and use .contains() to check whether the group
                contains surrounding -s then split it

            full example -> CPSC 1060:CPSC 1070!MATH 1070-MATH 1080!CPSC 1020-CPSC 1110
            ...means both CPSC 1060 AND 1070 are required, but only one of MATH 1070/1080 and CPSC 1020/1110
            is required.
        */

        String[] separatedOptionGroups = prereqList.split("!");
        String[] separatedOptions, separatedRequirements;

        //iterate through separatedOptionals
        //these will be very tiny arrays so don't rlly have to worry about time complexity like that
        for(int i = 0; i < separatedOptionGroups.length; i++) {
            //find whether each portion contains - or :

            if(separatedOptionGroups[i].contains(":")) {
                //: means these are all required.
                separatedRequirements = separatedOptionGroups[i].split(":");

                //simply iterate thru separated requirements and if one does not match return false
                for(int h = 0; h < separatedRequirements.length; h++) {
                    if(inputCodes.get(separatedRequirements[h]) == null) return false;
                }
            } else if(separatedOptionGroups[i].contains("-")) {
                //- means these are optional. only one has to be true
                separatedOptions = separatedOptionGroups[i].split("-");

                //now iterate through options and as long as one matches we are all good
                //use a flag to check if there is one match
                int flag = 0;
                for(int j = 0; j < separatedOptions.length; j++) {
                    if(inputCodes.get(separatedOptions[i]) != null) {
                        //if separatedOptions[i] exists in pre-existing course codes, then
                        //we can set the flag to 1. otherwise the flag will just stay at 0.
                        flag = 1;
                    }
                }

                if(flag == 0) return false;
            } else {
                //this case only happens if there is only one prerequisite
                //in this case compare to courseCodes and see whether one matches
                //hash maps make this easy !!!
                if(inputCodes.get(separatedOptionGroups[i]) == null) return false;
            }
        }

        //if false has not yet been returned we know everything is good, and we can return true :)
        return true;
    }

    //returns an array of corequisites (will mostly, if not always, be length of 1 or 2) for scheduler to use
    //may not even need yet idk?
    public String[] processCorequisites(String coreqList) {
        return coreqList.split(":");
    }
}
