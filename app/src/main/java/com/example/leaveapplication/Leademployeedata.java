package com.example.leaveapplication;

public class Leademployeedata {

    String empId,empName,empDep,empImageulr,mail;
    Leademployeedata(String empid,String empname,String empdep,String mail)
    {
        this.empId = "PSN "+empid;
        this.empName = empname;
        this.empDep = empdep;
        this.mail=mail;
    }

    public String getEmpId()
    {
        return empId;
    }

    public String getempEmail(){return mail;    }
    public String getEmpName()
    {
        return empName;
    }
    public String getEmpDep()
    {
        return empDep;
    }

}

