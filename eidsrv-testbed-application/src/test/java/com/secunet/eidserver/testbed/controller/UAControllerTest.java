/*
package com.secunet.eidserver.testbed.controller;

import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Collection;

import javax.ejb.EJBException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.exceptions.UserAlreadyExistsException;
import com.secunet.eidserver.testbed.common.exceptions.UserNotFoundException;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestProfile;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;
import com.secunet.eidserver.testbed.dao.UserDAO;
import com.secunet.eidserver.testbed.model.entities.UserEntity;
import com.secunet.eidserver.testbed.testhelper.ContainerProvider;

public final class UAControllerTest {
	private final TestHelper testHelper = new TestHelper();

	@BeforeClass
	public void setup() throws Exception {
		// nothing to do here
	}

	@AfterClass
	public void teardown() throws Exception {
		testHelper.deleteUsers();
	}

	@Test
	public void testCreateAccount() throws Exception {
		final UAControllerBean uaController = (UAControllerBean) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UAController");
		assertNotNull(uaController);

		assertEquals(testHelper.getUserList().size(), 0);
		assertEquals(testHelper.getUserList().size(), 0);

		// 1. Create an new account -> Must success.
		final UserDAO userDAO = (UserDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UserDAO");
		String result = uaController.createUser("user1@secunet.com", "123456", Role.ADMIN);
		assertNotNull(result);
		assertEquals(testHelper.getUserList().size(), 1);

		try {
			// 2. Create an account (name already present) -> Must fail.
			uaController.createUser("user1@secunet.com", "hallo", Role.TESTER);
		} catch (EJBException e) {
			assertTrue(e.getCause() instanceof UserAlreadyExistsException);
			return;
		}

		fail("UserAlreadyExistsException expected.");
	}

	@Test(dependsOnMethods = "testCreateAccount")
	public void testAssignProfile() throws Exception {
		final UserDAO userDAO = (UserDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UserDAO");
		assertNotNull(userDAO);
		final UAControllerBean uaController = (UAControllerBean) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UAController");
		final ProfileControllerBean profileController = (ProfileControllerBean) ContainerProvider.getContainer().getContext().lookup("java:global/classes/ProfileController");
		assertNotNull(profileController);

		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("./src/test/resources/ics_test.xml"), "UTF-8"));

		String xmlData = new String();

		try {
			String line;
			while ((line = in.readLine()) != null) {
				xmlData = xmlData.concat(line);
			}
		} finally {
			in.close();
		}

		TestProfile profile = profileController.createProfile(xmlData);

		uaController.assignProfile("user1@secunet.com", profile.getId());
		User user = userDAO.findByName("user1@secunet.com");
		assertNotNull(user.getProfiles());
		assertTrue(user.getProfiles().size() > 0);
	}

	@Test(dependsOnMethods = "testCreateAccount")
	public void testEditAccount() throws Exception {
		final UAControllerBean uaController = (UAControllerBean) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UAController");
		assertNotNull(uaController);

		testHelper.verifySecret("user1@secunet.com", "123456");
		uaController.setSecret("user1@secunet.com", "123456", "password");
		testHelper.verifySecret("user1@secunet.com", "password");
	}

	@Test(dependsOnMethods = "testEditAccount")
	public void testDeleteAccount() throws Exception {
		final UAControllerBean uaController = (UAControllerBean) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UAController");
		assertNotNull(uaController);

		assertEquals(testHelper.getUserList().size(), 1);
		assertEquals(testHelper.getUserList().size(), 1);

		uaController.deleteUser("user1@secunet.com");

		assertEquals(testHelper.getUserList().size(), 0);
		assertEquals(testHelper.getUserList().size(), 0);

		try {
			uaController.deleteUser("user1@secunet.com");
		} catch (EJBException e) {
			assertTrue(e.getCause() instanceof UserNotFoundException);
			return;
		}

		fail("UserNotFoudException expected.");
	}

	// -------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------

	public class TestHelper {

		*/
/**
		 * Get the list of all available accounts.
		 *
		 * @return
		 * @throws Exception
		 *//*

		public Collection<User> getUserList() throws Exception {
			final UserDAO userDAO = (UserDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UserDAO");
			assertNotNull(userDAO);

			return userDAO.findAll();
		}

		*/
/**
		 * Verify the given secret against the database.
		 *
		 * @param accountName
		 *            The name of the account to verify.
		 * @param secret
		 *            The secret to check.
		 * @throws Exception
		 *//*

		public void verifySecret(final String accountName, final String secret) throws Exception {
			final UserDAO userDAO = (UserDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UserDAO");
			assertNotNull(userDAO);

			User user = userDAO.findByName(accountName);
			assertNotNull(user);

			assertEquals(user.getPwdHash(), new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(secret.getBytes())).toString(16));
		}

		*/
/**
		 * @throws Exception
		 *//*

		public void deleteUsers() throws Exception {
			final UserDAO userDAO = (UserDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UserDAO");
			assertNotNull(userDAO);

			Collection<UserEntity> accountEntities = userDAO.findAll();
			accountEntities.forEach(userDAO::delete);
			assertEquals(userDAO.findAll().size(), 0);
		}
	}
}
*/
