import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Application {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String args[]){
        String fileName = "courses.txt";
        List<String> lines = readFile(fileName);

        if(lines != null){
            System.out.println("Please enter your name");
            String userName = sc.nextLine();

            List<Course> courses = parseCourses(lines);

            System.out.println("List of available courses");
            printCourses(courses);

            //Perform mentioned operations on courses
            operations(courses);

            //Write new content to file
            writeToFile(fileName, courses);

            //Favorites list
            favorites(courses, userName);
        }
    }

    /**
     * Function to perform basic operations like add, edit, list, search
     * @param courses List of courses
     */
    private static void operations(List<Course> courses){
        boolean flag = true;

        while(flag){
            System.out.println("Please select a choice. \n 1. Add course \n 2. Edit course \n 3. List course \n 4. Search course \n 5. Exit");
            int choice = numberInput(null);

            switch (choice){
                case 1: addCourse(courses);
                    break;
                case 2: editCourse(courses);
                    break;
                case 3: printCourses(courses);
                    break;
                case 4: searchCourse(courses);
                    break;
                case 5: flag = false;
                    break;
                default: System.out.println("Invalid selection please choose again.");
            }

            if(!flag){
                System.out.println("Do you want to make additional operations ? Enter 'yes' or 'no'");
                String option = sc.nextLine().trim().toLowerCase();

                switch(option){
                    case "yes": flag = true;
                        break;
                    case "no": break;
                    default: System.out.println("Invalid input. Exiting");
                        break;
                }
            }
        }
    }

    private static void favorites(List<Course> courses, String userName){
        System.out.println("Create a favorites list by entering comma separated course ids");

        while(true) {
            try {
                String line = sc.nextLine().trim();
                List<Integer> favIds = Stream.of(line.split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());

                List<Course> favCourses = courses.stream().filter(x -> favIds.stream().anyMatch(y -> x.getCourseId() == y)).collect(Collectors.toList());
                printCourses(favCourses);

                int hours = favCourses.stream().mapToInt(Course::getCourseLength).sum();

                System.out.println(String.format("%s has signed up for %s courses with %s credits", userName, favCourses.size(), hours));
                break;
            } catch (NumberFormatException ex){
                System.out.println("Please enter comma separated ids. No alphabets allowed");
            }
        }

    }

    /**
     * Function to read file
     * @param fileName File name
     * @return List of lines in file
     */
    private static List<String> readFile(String fileName){
        List<String> lines = null;

        try {
            //Try reading file from resources
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(fileName);

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            lines = new ArrayList<>();

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                //Add all lines to List
                lines.add(line);
            }

            br.close();
            is.close();
        } catch (IOException e){
            //Critical error, terminate application
            System.err.println("Error: Target File Cannot Be Read");
            System.exit(0);
        }

        return lines;
    }

    /**
     * Function to write to file after modifications are made
     * @param fileName File Name
     * @param courses List of courses
     */
    private static void writeToFile(String fileName, List<Course> courses){
        try{
            PrintWriter pw = new PrintWriter(new File(Application.class.getResource(fileName).getPath()));
            for(Course course : courses){
                pw.println(String.format("%s %s %s %s", course.getCourseId(), course.getCourseName(), course.getCourseLength(), course.getCourseSubject()));
            }
            pw.close();
        } catch (FileNotFoundException ex){
            System.err.println("Error writing to file !");
        }
    }

    /**
     * Function to create course objects from string lines
     * @param lines Lines of string
     * @return List of course objects
     */
    private static List<Course> parseCourses(List<String> lines){
        List<Course> courses = new ArrayList<>();

        for(String line : lines){
            int courseId = Integer.parseInt(line.substring(0, 5));
            String[] courseData = line.substring(5).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

            if(courseData.length == 3) {
                Course course = new Course(courseId, courseData[0].trim(), Integer.parseInt(courseData[1]), courseData[2].trim());
                courses.add(course);
            } else{
                System.out.println(String.format("Error loading course with course id %s", courseId));
            }
        }
        return courses;
    }

    /**
     * Function to print courses
     * @param courses List of course objects
     */
    private static void printCourses(List<Course> courses) {
        if (courses != null && courses.size() > 0){
            System.out.println("Course ID | Course Name \t\t\t | Course Length | Course Subject");

            for (Course course : courses) {
                System.out.println(String.format("%-10s %-27s %-14s %s", course.getCourseId(), course.getCourseName(),
                        course.getCourseLength(), course.getCourseSubject()));
            }
        } else{
            System.out.println("No courses found");
        }
        System.out.println();
    }

    /**
     * Function to add course to list
     * @param courses List of courses
     */
    private static void addCourse(List<Course> courses){
        int courseId, courseLength;
        String courseName, courseSubject;

        //List of existing Course Ids
        List<Integer> courseIds = courses.stream().map(Course::getCourseId).collect(Collectors.toList());

        System.out.println("Enter course id (Integers only)");
        // Check for uniqueness of course id input
        courseId = numberInput(courseIds);

        System.out.println("Enter course name");
        courseName = sc.nextLine();

        System.out.println("Enter course length (Integers only)");
        courseLength = numberInput(null);

        System.out.println("Enter course name");
        courseSubject = sc.nextLine();

        courses.add(new Course(courseId, courseName, courseLength, courseSubject));
        System.out.println("Added new course to list");
    }

    /**
     * Function to edit course from list
     * @param courses List of courses
     */
    private static void editCourse(List<Course> courses){
        //List of existing Course Ids
        List<Integer> courseIds = courses.stream().map(Course::getCourseId).collect(Collectors.toList());

        System.out.println("List of available courses");
        printCourses(courses);

        while(true){
            System.out.println("Enter course id to edit");
            int input = numberInput(null);

            if(courseIds.contains(input)){
                Course course = courses.stream().filter(x -> x.getCourseId() == input).findFirst().orElse(null);

                System.out.println("Enter field to edit \n 1. Course Name \n 2. Course Length \n 3. Course Subject \n 4. Exit");
                int fieldNumber = numberInput(null);

                switch (fieldNumber){
                    case 1: System.out.println("Enter new course name");
                            course.setCourseName(sc.nextLine());
                            System.out.println(String.format("Updated course name for course id: %s to %s", input, course.getCourseName()));
                            return;
                    case 2: System.out.println("Enter new course length");
                            course.setCourseLength(numberInput(null));
                            System.out.println(String.format("Updated course length for course id: %s to %s", input, course.getCourseLength()));
                            return;
                    case 3: System.out.println("Enter new course subject");
                            course.setCourseSubject(sc.nextLine());
                            System.out.println(String.format("Updated course subject for course id: %s to %s", input, course.getCourseSubject()));
                            return;
                    case 4: return;
                    default: System.out.println("Invalid selection please choose again.");
                }
            } else {
                System.out.println("Course id doesn't exist. Please try again");
            }
        }
    }

    /**
     * Function to search courses from list
     * @param courses List of courses
     */
    private static void searchCourse(List<Course> courses){
        while(true) {
            System.out.println("Enter field to search on \n 1. Course Id \n 2. Course Name \n 3. Course Length \n 4. Course Subject \n 5. Exit");

            int input = numberInput(null);

            switch(input){
                case 1: System.out.println("Enter course id");
                        int id = numberInput(null);
                        List<Course> result = courses.stream().filter(x -> x.getCourseId() == id).collect(Collectors.toList());
                        printCourses(result);
                        break;
                case 2: System.out.println("Enter course name");
                        String name = sc.nextLine();
                        List<Course> result2 = courses.stream().filter(x -> x.getCourseName().equalsIgnoreCase(name)).collect(Collectors.toList());
                        printCourses(result2);
                        break;
                case 3: System.out.println("Enter course length");
                        int length = numberInput(null);
                        List<Course> result3 = courses.stream().filter(x -> x.getCourseLength() == length).collect(Collectors.toList());
                        printCourses(result3);
                        break;
                case 4: System.out.println("Enter course subject");
                        String subject = sc.nextLine();
                        List<Course> result4 = courses.stream().filter(x -> x.getCourseSubject().equalsIgnoreCase(subject)).collect(Collectors.toList());
                        printCourses(result4);
                        break;
                case 5: return;
                default: System.out.println("Invalid selection please choose again.");
            }
        }
    }

    /**
     * Function for retrieving number input
     * @param uniqueIds (optional) Check input for uniqueness against paramter
     * @return integer
     */
    private static int numberInput(List<Integer> uniqueIds){
        int num = 0;

        while(true){
            try{
                num = Integer.parseInt(sc.nextLine());

                // This logic is specifically for course id
                if(uniqueIds != null) {
                    if(String.valueOf(num).length() < 5){
                        System.out.println("Course ID length cannot be less than 5 digits. Please try again");
                    } else if(uniqueIds.contains(num)) {
                        System.out.println("Course ID already exists. Please choose another course id");
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            } catch (NumberFormatException ex){
                System.out.println("Please enter numbers only. Try again");
                sc.reset();
            }
        }

        return num;
    }
}