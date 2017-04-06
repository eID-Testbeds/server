// TODO reenable

// package com.secunet.eidserver.testbed.web.domain.test;
//
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Random;
//
// import com.secunet.eidserver.testbed.common.classes.TestModule;
// import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
// import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
// import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
//
// public class TestRunModelMother
// {
//
//
// static TestRunModel generateTestModuleTree(TestCandidate candidate, int depth, int width)
// {
// TestModule moduleRoot = TestModuleMother.generateTestModuleTree(depth, width, candidate);
//
//
// TestRunModel testRun = new TestRunModel();
// testRun.setCandidate(candidate);
// Map<TestCase, Integer> testProgress = new HashMap<>();
// Map<TestCase, Map<TestCaseStep, Boolean>> successMapping = new HashMap<>();
// testRun.setProgress(testProgress);
// testRun.setSuccessMapping(successMapping);
//
// generateProgressAndSuccess(moduleRoot, testProgress, successMapping);
//
//
// return testRun;
// }
//
// private static void generateProgressAndSuccess(TestModule moduleRoot, Map<TestCase, Integer> testProgress, Map<TestCase, Map<TestCaseStep, Boolean>> successMapping)
// {
// Random r = new Random();
// for (TestCase tc : moduleRoot.getChildTestcases())
// {
// testProgress.put(tc, 1);
// if (!successMapping.containsKey(tc))
// successMapping.put(tc, new HashMap<>());
//
// for (TestCaseStep step : tc.getTestCaseSteps())
// successMapping.get(tc).put(step, r.nextBoolean());
// }
//
// for (TestModule module : moduleRoot.getChildModules())
// {
// generateProgressAndSuccess(module, testProgress, successMapping);
// }
// }
//
// }
