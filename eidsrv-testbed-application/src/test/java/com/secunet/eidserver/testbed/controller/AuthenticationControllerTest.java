/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
package com.secunet.eidserver.testbed.controller;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNotSame;
import static org.testng.AssertJUnit.fail;

import java.util.Collection;
import java.util.Date;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;
import com.secunet.eidserver.testbed.dao.TokenDAO;
import com.secunet.eidserver.testbed.dao.UserDAO;
import com.secunet.eidserver.testbed.model.entities.TokenEntity;
import com.secunet.eidserver.testbed.model.entities.UserEntity;
import com.secunet.eidserver.testbed.testhelper.ContainerProvider;

*//**
 *
 *//*
public class AuthenticationControllerTest {
	private final TestHelper testHelper = new TestHelper();

	@BeforeClass
	public void setup() throws Exception {
		testHelper.createUsers();
	}

	@AfterClass
	public void teardown() throws Exception {
		testHelper.deleteUsers();
	}

	@Test
	public void testAuthenticateUser() throws Exception {
		final AuthenticationControllerBean authenticationController = (AuthenticationControllerBean) ContainerProvider.getContainer().getContext()
				.lookup("java:global/classes/AuthenticationController");
		assertNotNull(authenticationController);

		final TokenDAO tokenDAO = (TokenDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TokenDAO");
		assertNotNull(tokenDAO);

		final String token1String = authenticationController.authenticateUser("user1@secunet.com", "123456");
		assertNotNull(token1String);
		Token token1 = tokenDAO.findByToken(token1String);
		assertNotNull(token1);

		final User user1 = token1.getUser();
		assertNotNull(user1);
		assertEquals(user1.getRole(), Role.ADMIN);
		assertEquals(user1.getName(), "user1@secunet.com");

		final String token2String = authenticationController.authenticateUser("user2@secunet.com", "password");
		assertNotNull(token2String);
		Token token2 = tokenDAO.findByToken(token2String);
		assertNotNull(token2);

		final User user2 = token2.getUser();
		assertNotNull(user2);
		assertEquals(user2.getRole(), Role.PROFILE_CREATOR);
		assertEquals(user2.getName(), "user2@secunet.com");

		final String token3String = authenticationController.authenticateUser("user3@secunet.com", "hallo");
		assertNotNull(token3String);
		Token token3 = tokenDAO.findByToken(token3String);
		assertNotNull(token3);

		final User user3 = token3.getUser();
		assertNotNull(user3);
		assertEquals(user3.getRole(), Role.TESTER);
		assertEquals(user3.getName(), "user3@secunet.com");
	}

	@Test(dependsOnMethods = "testAuthenticateUser")
	public void testDoubleAuthentication() throws Exception {
		final AuthenticationControllerBean authenticationController = (AuthenticationControllerBean) ContainerProvider.getContainer().getContext()
				.lookup("java:global/classes/AuthenticationController");
		assertNotNull(authenticationController);

		final TokenDAO tokenDAO = (TokenDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TokenDAO");
		assertNotNull(tokenDAO);

		final String token1String = authenticationController.authenticateUser("user1@secunet.com", "123456");
		assertNotNull(token1String);
		Token token1 = tokenDAO.findByToken(token1String);
		assertNotNull(token1);

		final String token2String = authenticationController.authenticateUser("user2@secunet.com", "password");
		assertNotNull(token2String);
		Token token2 = tokenDAO.findByToken(token2String);
		assertNotNull(token2);

		assertNotSame(token1.getToken(), token2.getToken());

		testHelper.modifyLoginDate("user1@secunet.com");

		final String token3String = authenticationController.authenticateUser("user3@secunet.com", "hallo");
		assertNotNull(token3String);
		Token token3 = tokenDAO.findByToken(token3String);
		assertNotNull(token3);

		if (0 == token1.getToken().compareToIgnoreCase(token3.getToken()))
			fail("Token not refreshed.");
	}

	public class TestHelper {
		public void createUsers() throws Exception {
			final UAControllerBean uaController = (UAControllerBean) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UAController");
			assertNotNull(uaController);

			assertEquals(testHelper.getUserList().size(), 0);
			assertEquals(testHelper.getUserList().size(), 0);

			String user1 = uaController.createUser("user1@secunet.com", "123456", Role.ADMIN);
			assertNotNull(user1);
			assertEquals(testHelper.getUserList().size(), 1);

			String user2 = uaController.createUser("user2@secunet.com", "password", Role.PROFILE_CREATOR);
			assertNotNull(user2);
			assertEquals(testHelper.getUserList().size(), 2);

			String user3 = uaController.createUser("user3@secunet.com", "hallo", Role.TESTER);
			assertNotNull(user3);
			assertEquals(testHelper.getUserList().size(), 3);
		}

		public void deleteUsers() throws Exception {
			final UserDAO userDAO = (UserDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UserDAO");
			assertNotNull(userDAO);

			Collection<UserEntity> accountEntities = userDAO.findAll();
			accountEntities.forEach(userDAO::delete);
			assertEquals(userDAO.findAll().size(), 0);
		}

		*//**
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

		public void modifyLoginDate(final String userName) throws Exception {
			final UserDAO userDAO = (UserDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/UserDAO");
			final TokenDAO tokenDAO = (TokenDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TokenDAO");

			final User user = userDAO.findByName(userName);
			assertNotNull(user);

			final Token token = tokenDAO.findByUser(user);
			assertNotNull(token);

			final long modifiedDate = new Date().getTime() - 432000001;
			token.setLoginDate(new Date(modifiedDate));
			tokenDAO.update((TokenEntity) token);

			Thread.sleep(2000); // We have to wait some time otherwise we will
								// get the same token again :( -> Test fails
		}
	}
}*/
