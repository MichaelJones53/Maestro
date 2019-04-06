package com.mikejones.maestro;

import java.util.ArrayList;

public interface IClassCreateable {
    void onClassCreated(String classId);
    void onUpdateClassList(ArrayList<UserClass> classes);

}
