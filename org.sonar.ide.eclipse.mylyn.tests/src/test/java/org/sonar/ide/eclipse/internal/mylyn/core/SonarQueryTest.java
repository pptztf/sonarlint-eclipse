/*
 * Sonar Eclipse
 * Copyright (C) 2010-2013 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.ide.eclipse.internal.mylyn.core;

import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class SonarQueryTest {
  private IRepositoryQuery query;

  @Before
  public void setUp() {
    query = new RepositoryQuery(SonarConnector.CONNECTOR_KIND, "");
  }

  @Test
  public void testGetStatuses() {
    query.setAttribute(SonarQuery.STATUSES, "OPEN");
    assertThat(SonarQuery.getStatuses(query), is(new String[] {"OPEN"}));

    query.setAttribute(SonarQuery.STATUSES, "OPEN,REOPENED");
    assertThat(SonarQuery.getStatuses(query), is(new String[] {"OPEN", "REOPENED"}));
  }

  @Test
  public void testGetSeverities() {
    query.setAttribute(SonarQuery.SEVERITIES, "BLOCKER");
    assertThat(SonarQuery.getSeverities(query), is(new String[] {"BLOCKER"}));

    query.setAttribute(SonarQuery.SEVERITIES, "BLOCKER,MAJOR");
    assertThat(SonarQuery.getSeverities(query), is(new String[] {"BLOCKER", "MAJOR"}));
  }

  @Test
  public void testGetReporter() {
    try {
      SonarQuery.getReporter(query, "foo");
    } catch (IllegalStateException e) {
      // expected
    }

    query.setAttribute(SonarQuery.REPORTER, SonarQuery.ANY_USER);
    assertThat(SonarQuery.getReporter(query, "foo"), nullValue());

    query.setAttribute(SonarQuery.REPORTER, SonarQuery.UNASSIGNED);
    assertThat(SonarQuery.getReporter(query, "foo"), is(new String[] {""}));

    query.setAttribute(SonarQuery.REPORTER, SonarQuery.CURRENT_USER);
    assertThat(SonarQuery.getReporter(query, "foo"), is(new String[] {"foo"}));

    query.setAttribute(SonarQuery.REPORTER, SonarQuery.SPECIFIED_USER);
    query.setAttribute(SonarQuery.REPORTER_USER, "bar");
    assertThat(SonarQuery.getReporter(query, "foo"), is(new String[] {"bar"}));
  }

  @Test
  public void testGetAssignee() {
    try {
      SonarQuery.getAssignee(query, "foo");
    } catch (IllegalStateException e) {
      // expected
    }

    query.setAttribute(SonarQuery.ASSIGNEE, SonarQuery.ANY_USER);
    assertThat(SonarQuery.getAssignee(query, "foo"), nullValue());

    query.setAttribute(SonarQuery.ASSIGNEE, SonarQuery.UNASSIGNED);
    assertThat(SonarQuery.getAssignee(query, "foo"), is(new String[] {""}));

    query.setAttribute(SonarQuery.ASSIGNEE, SonarQuery.CURRENT_USER);
    assertThat(SonarQuery.getAssignee(query, "foo"), is(new String[] {"foo"}));

    query.setAttribute(SonarQuery.ASSIGNEE, SonarQuery.SPECIFIED_USER);
    query.setAttribute(SonarQuery.ASSIGNEE_USER, "bar");
    assertThat(SonarQuery.getAssignee(query, "foo"), is(new String[] {"bar"}));
  }

}
