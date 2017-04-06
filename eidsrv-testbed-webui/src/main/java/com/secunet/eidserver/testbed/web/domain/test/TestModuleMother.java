// TODO reenable


// package com.secunet.eidserver.testbed.web.domain.test;
//
// import java.math.BigInteger;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Random;
// import java.util.UUID;
//
// import com.secunet.eidserver.testbed.common.classes.TestModule;
// import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
// import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
// import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
// import com.secunet.eidserver.testbed.common.types.step.Attribute;
// import com.secunet.eidserver.testbed.common.types.step.Attributes;
// import com.secunet.eidserver.testbed.common.types.step.Step;
// import com.secunet.eidserver.testbed.common.types.step.StepToken;
// import com.secunet.eidserver.testbed.model.entities.DefaultTestCaseEntity;
// import com.secunet.eidserver.testbed.model.entities.TestCaseStepEntity;
// import com.secunet.testbedutils.utilities.JaxBUtil;
//
// public class TestModuleMother
// {
//
// static Random r = new Random();
//
// static TestModule generateTestModuleTree(int depth, int width, TestCandidate cand)
// {
// TestModule root = new TestModule();
// root.setName("Rootnode (" + depth + " / " + width + ")");
//
// generateTestModuleTree(root, 0, depth, width, cand);
//
// return root;
// }
//
// private static void generateTestModuleTree(TestModule root, int depth, int depthMax, int width, TestCandidate cand)
// {
// if (depth >= depthMax)
// {
// List<TestCase> testCases = generateTestCases(width, cand);
// root.setChildTestcases(testCases);
// }
// else
// {
// List<TestModule> modules = generateTestModules(depth, width);
// if (depth < depthMax)
// {
// for (TestModule module : modules)
// generateTestModuleTree(module, (depth + 1), depthMax, width, cand);
// }
//
// root.setChildModules(modules);
// }
// }
//
// private static List<TestModule> generateTestModules(int depth, int width)
// {
// List<TestModule> modules = new ArrayList<>(width);
//
// for (int i = 0; i < width; i++)
// {
// TestModule m = new TestModule();
// m.setName("TestModule: " + depth + "(" + width + ")");
// modules.add(m);
// }
//
// return modules;
// }
//
// private static List<TestCase> generateTestCases(int width, TestCandidate cand)
// {
// List<TestCase> tcs = new ArrayList<>();
//
// for (int i = 0; i < width; i++)
// {
// TestCase tc = new DefaultTestCaseEntity();
// tc.setCertificateBaseNames(Arrays.asList(new String[] { "name1", "name2" }));
// tc.setName("Test case " + i + " " + UUID.randomUUID().toString());
// tc.setSaml(false);
// tc.setDescription("descr " + tc.getName());
// ArrayList<TestCaseStep> steps = new ArrayList<>();
// tc.setTestCaseSteps(steps);
// tc.setCandidates(Arrays.asList(cand));
//
// for (int j = 0; j < width; j++)
// steps.add(generateTestCaseStep(j));
//
// tcs.add(tc);
// }
//
// return tcs;
// }
//
// private static TestCaseStep generateTestCaseStep(int j)
// {
// TestCaseStepEntity tcse = new TestCaseStepEntity();
// tcse.setMessage(generateStepMessage(j));
// tcse.setXsdName("<this is the content of the xsd>");
// tcse.setOptional(r.nextBoolean());
// tcse.setDefault(r.nextBoolean());
// tcse.setInbound(r.nextBoolean());
// tcse.setName("Step " + j + " I=" + tcse.getInbound());
//
// return tcse;
// }
//
// private static String generateStepMessage(int j)
// {
// Step step = new Step();
// step.setSchema("Schema 1 " + j);
//
// StepToken httpToken1 = new StepToken();
// httpToken1.setIsMandatory(r.nextBoolean());
// httpToken1.setMaxNumberOfOccurences(new BigInteger("" + r.nextInt(10)));
// httpToken1.setName("token name 1 " + j);
// httpToken1.setValue("value 1 " + j);
// StepToken httpToken2 = new StepToken();
// httpToken2.setIsMandatory(r.nextBoolean());
// httpToken2.setMaxNumberOfOccurences(new BigInteger("" + r.nextInt(10)));
// httpToken2.setName("token name 2 " + j);
// httpToken2.setValue("value 2 " + j);
//
// step.getHttpStepToken().add(httpToken1);
// step.getHttpStepToken().add(httpToken2);
//
// StepToken protToken1 = new StepToken();
// protToken1.setIsMandatory(r.nextBoolean());
// protToken1.setMaxNumberOfOccurences(new BigInteger("" + r.nextInt(10)));
// protToken1.setName("ptoken name 1 " + j);
// protToken1.setValue("pvalue 1 " + j);
// Attribute attr1 = new Attribute();
// attr1.setName("attr name");
// attr1.setValue("attr val");
// Attributes atts1 = new Attributes();
// protToken1.setAttributes(atts1);
// atts1.getAttribute().add(attr1);
//
// StepToken protToken2 = new StepToken();
// protToken2.setIsMandatory(r.nextBoolean());
// protToken2.setMaxNumberOfOccurences(new BigInteger("" + r.nextInt(10)));
// protToken2.setName("p tok name 2 " + j);
// protToken2.setValue("p value 2 " + j);
// Attribute attr2 = new Attribute();
// attr2.setName("attr2 name");
// attr2.setValue("attr2 val");
// Attributes atts2 = new Attributes();
// protToken2.setAttributes(atts2);
// atts2.getAttribute().add(attr2);
//
// step.getProtocolStepToken().add(protToken2);
//
//
// String marshall = JaxBUtil.marshall(step);
// return marshall;
// }
// }
