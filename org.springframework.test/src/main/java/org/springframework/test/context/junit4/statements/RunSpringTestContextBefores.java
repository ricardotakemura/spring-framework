/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.junit4.statements;

import java.lang.reflect.Method;

import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;

/**
 * <code>RunSpringTestContextBefores</code> is a custom JUnit 4.5+
 * {@link Statement} which allows the <em>Spring TestContext Framework</em> to
 * be plugged into the JUnit execution chain by calling
 * {@link TestContextManager#beforeTestMethod(Object, Method)
 * beforeTestMethod()} on the supplied {@link TestContextManager}.
 * 
 * @see #evaluate()
 * @see RunSpringTestContextAfters
 * @author Sam Brannen
 * @since 3.0
 */
public class RunSpringTestContextBefores extends Statement {

	private final Statement next;

	private final Object testInstance;

	private final Method testMethod;

	private final TestContextManager testContextManager;


	/**
	 * Constructs a new <code>RunSpringTestContextBefores</code> statement.
	 * 
	 * @param next the next <code>Statement</code> in the execution chain
	 * @param testInstance the current test instance (never <code>null</code>)
	 * @param testMethod the test method which is about to be executed on the
	 * test instance
	 * @param testContextManager the TestContextManager upon which to call
	 * <code>beforeTestMethod()</code>
	 */
	public RunSpringTestContextBefores(Statement next, Object testInstance, Method testMethod,
			TestContextManager testContextManager) {
		this.next = next;
		this.testInstance = testInstance;
		this.testMethod = testMethod;
		this.testContextManager = testContextManager;
	}

	/**
	 * Calls {@link TestContextManager#beforeTestMethod(Object, Method)} and
	 * then invokes the next {@link Statement} in the execution chain (typically
	 * an instance of {@link org.junit.internal.runners.statements.RunBefores
	 * RunBefores} ).
	 */
	@Override
	public void evaluate() throws Throwable {
		this.testContextManager.beforeTestMethod(this.testInstance, this.testMethod);
		this.next.evaluate();
	}

}