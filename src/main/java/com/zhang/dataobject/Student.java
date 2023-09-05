package com.zhang.dataobject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Yaohang Zhang
 * @ClassName Student
 * @description
 * @date 2023/9/4 15:16
 */
@Slf4j
public class Student {


    private String studentNo;

    private String name;

    @Override
    public String toString() {
        return "Student{" +
                "studentNo='" + studentNo + '\'' +
                ", name='" + name + '\'' +
                ", clazz='" + clazz + '\'' +
                '}';
    }

    private String clazz;

    public Student () {

    }


    public Student(String studentNo, String name, String clazz) {

        this.studentNo = studentNo;
        this.name = name;
        this.clazz = clazz;
        log.info("执行了构造器方法");
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void study() {
        log.info("{}正在执行学习方法", this.name);
    }
}
