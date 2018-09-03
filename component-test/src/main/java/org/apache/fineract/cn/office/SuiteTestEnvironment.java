package org.apache.fineract.cn.office;

import org.apache.fineract.cn.test.env.TestEnvironment;
import org.apache.fineract.cn.test.fixture.cassandra.CassandraInitializer;
import org.apache.fineract.cn.test.fixture.mariadb.MariaDBInitializer;
import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.rules.RunExternalResourceOnce;
import org.junit.rules.TestRule;

public class SuiteTestEnvironment {

  static final String APP_NAME = "office-v1";
  static final TestEnvironment testEnvironment = new TestEnvironment(APP_NAME);
  static final CassandraInitializer cassandraInitializer = new CassandraInitializer();
  static final MariaDBInitializer mariaDBInitializer = new MariaDBInitializer();

  @ClassRule
  public static TestRule orderClassRules = RuleChain
          .outerRule(new RunExternalResourceOnce(testEnvironment))
          .around(new RunExternalResourceOnce(cassandraInitializer))
          .around(new RunExternalResourceOnce(mariaDBInitializer));
}
