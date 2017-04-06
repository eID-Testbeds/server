// TODO reenable

// package com.secunet.eidserver.testbed.web.domain.test;
//
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Random;
// import java.util.Set;
//
// import javax.annotation.Nonnull;
//
// import com.secunet.eidserver.testbed.common.classes.TestModule;
// import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
// import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
// import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
//
// public class TestBackend
// {
//
// static HashMap<String, TestCandidate> candidatesMap = new HashMap<>();
//
// static HashMap<TestCandidate, TestModule> candidates2TestSetupTreeMap = new HashMap<>();
// static HashMap<TestCandidate, TestRunModel> candidates2RunningTests = new HashMap<>();
// static HashMap<TestCandidate, List<TestRunModel>> candidates2ExecutedRuns = new HashMap<>();
//
//
// static
// {
// for (int c = 0; c < 11; c++)
// {
// TestCandidate cand = TestCandidateMother.generateDemoCandidate(c);
// candidatesMap.put(cand.getId(), cand);
//
//
// TestModule root = TestModuleMother.generateTestModuleTree(2, 3, cand);
// candidates2TestSetupTreeMap.put(cand, root);
// // every 2nd is running
// if (c % 2 == 0)
// {
// cand.setCandidateName(cand.getCandidateName() + " R");
//
// Map<TestCase, Integer> progress = new HashMap<>();
// fillProgressWithTestCasesFrom(root, progress, new HashMap<>());
// TestRunModel testRunModel = new TestRunModel();
// testRunModel.setCandidate(cand);
// testRunModel.setProgress(progress);
// candidates2RunningTests.put(cand, testRunModel);
// }
//
// // every 3rd has some already run data
// if (c % 3 == 0)
// {
// List<TestRunModel> runTests = new ArrayList<>();
// candidates2ExecutedRuns.put(cand, runTests);
// cand.setCandidateName(cand.getCandidateName() + " x");
//
// for (int doneRuns = 0; doneRuns < 4; doneRuns++)
// {
// Map<TestCase, Map<TestCaseStep, Boolean>> actualSuccess = new HashMap<>();
// Map<TestCase, Integer> progress = new HashMap<>();
// fillProgressWithTestCasesFrom(root, progress, actualSuccess);
// TestRunModel testRunModel = new TestRunModel();
// testRunModel.setCandidate(cand);
// testRunModel.setProgress(progress);
// testRunModel.setSuccessMapping(actualSuccess);
//
// runTests.add(testRunModel);
// }
//
// }
// }
// }
//
// static Set<TestCase> getAllTestCases()
// {
// HashSet<TestCase> tcs = new HashSet<>();
//
// candidates2TestSetupTreeMap.values().forEach(tc -> extractTestCases(tc, tcs));
//
// return tcs;
// }
//
// private static void extractTestCases(TestModule tc, HashSet<TestCase> tcs)
// {
// tcs.addAll(tc.getChildTestcases());
//
// tc.getChildModules().forEach(ctc -> extractTestCases(ctc, tcs));
// }
//
// private static void fillProgressWithTestCasesFrom(TestModule root, Map<TestCase, Integer> progresses, Map<TestCase, Map<TestCaseStep, Boolean>> actualSuccess)
// {
// for (TestModule child : root.getChildModules())
// fillProgressWithTestCasesFrom(child, progresses, actualSuccess);
//
// for (TestCase tc : root.getChildTestcases())
// {
// int progress = new Random().nextInt(10) * 10;
// progresses.put(tc, progress);
// Map<TestCaseStep, Boolean> steps = new HashMap<>();
//
// for (TestCaseStep step : tc.getTestCaseSteps())
// steps.put(step, (progress == 100));
//
// actualSuccess.put(tc, steps);
// }
// }
//
// static @Nonnull Set<TestCaseStep> getAllTestCaseSteps()
// {
// HashSet<TestCaseStep> steps = new HashSet<>();
//
// getAllTestCases().forEach(tc -> steps.addAll(tc.getTestCaseSteps()));
//
// return steps;
// }
//
// }
