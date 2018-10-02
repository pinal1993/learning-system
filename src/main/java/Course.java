public class Course {
    private int courseId;

    private String courseName;

    private int courseLength;

    private String courseSubject;

    public Course(int courseId, String courseName, int courseLength, String courseSubject){
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseLength = courseLength;
        this.courseSubject = courseSubject;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseLength() {
        return courseLength;
    }

    public void setCourseLength(int courseLength) {
        this.courseLength = courseLength;
    }

    public String getCourseSubject() {
        return courseSubject;
    }

    public void setCourseSubject(String courseSubject) {
        this.courseSubject = courseSubject;
    }
}
