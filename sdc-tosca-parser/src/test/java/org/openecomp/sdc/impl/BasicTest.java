package org.openecomp.sdc.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

public class BasicTest {

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setup(){
        System.out.println("#### Starting Test " + testName.getMethodName() + " ###########");
    }

    @After
    public void tearDown(){
        System.out.println("#### Ended test " + testName.getMethodName() + " ###########");
    }
}
