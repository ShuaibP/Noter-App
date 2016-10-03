package com.uct.noter.noter;


/**
 * Created by Shuaib on 2016-08-14.
 * Question, is a type of annotation but behaves differently.
 */
public class Question extends Annotation {

    public Question (String description) {
        super(description);
    }

    public Question(){}
}
