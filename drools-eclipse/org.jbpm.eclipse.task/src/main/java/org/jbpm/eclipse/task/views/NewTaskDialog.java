/*
 * Copyright 2010 JBoss Inc
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

package org.jbpm.eclipse.task.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jbpm.task.AccessType;
import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.service.ContentData;


public class NewTaskDialog extends Dialog {

    private Task task;
    private ContentData content;

    private Text nameText;
    private Text actorText;
    private Text subjectText;
    private Text commentText;
    private Text priorityText;
    private Button skippableButton;
    private Text contentText;
    private Text localeText;

    public NewTaskDialog(Shell shell) {
        super(shell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Create New Task");
    }
    
    protected Point getInitialSize() {
        return new Point(450, 350);
    }
    
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        composite.setLayout(gridLayout);
        Label label = new Label(composite, SWT.NONE);
        label.setText("Name: ");
        nameText = new Text(composite, SWT.NONE);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        nameText.setLayoutData(gridData);
        
        label = new Label(composite, SWT.NONE);
        label.setText("Potential owner(s): ");
        actorText = new Text(composite, SWT.NONE);
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        actorText.setLayoutData(gridData);
        
        label = new Label(composite, SWT.NONE);
        label.setText("Subject: ");
        subjectText = new Text(composite, SWT.NONE);
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        subjectText.setLayoutData(gridData);
        
        label = new Label(composite, SWT.NONE);
        label.setText("Comment: ");
        commentText = new Text(composite, SWT.MULTI);
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        commentText.setLayoutData(gridData);
        
        label = new Label(composite, SWT.NONE);
        label.setText("Priority: ");
        priorityText = new Text(composite, SWT.NONE);
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        priorityText.setLayoutData(gridData);
        
        skippableButton = new Button(composite, SWT.CHECK | SWT.LEFT);
        skippableButton.setText("Skippable");
        gridData = new GridData();
        gridData.horizontalSpan = 2;
        skippableButton.setLayoutData(gridData);
        
        label = new Label(composite, SWT.NONE);
        label.setText("Content: ");
        contentText = new Text(composite, SWT.MULTI);
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        contentText.setLayoutData(gridData);
        
        label = new Label(composite, SWT.NONE);
        label.setText("Locale: ");
        localeText = new Text(composite, SWT.MULTI);
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        localeText.setLayoutData(gridData);
        localeText.setText("en-UK");
        
        return composite;
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            createTask();
        }
        super.buttonPressed(buttonId);
    }

    private void createTask() {
        task = new Task();
        String locale = localeText.getText();
        if (locale == null || locale.length() == 0) {
        	locale = "en-UK";
        }
        String taskName = nameText.getText();
        List<I18NText> names = new ArrayList<I18NText>();
        names.add(new I18NText(locale, taskName));
        task.setNames(names);
        String subject = subjectText.getText();
        List<I18NText> subjects = new ArrayList<I18NText>();
        subjects.add(new I18NText(locale, subject));
        task.setSubjects(subjects);
        String comment = commentText.getText();
        List<I18NText> descriptions = new ArrayList<I18NText>();
        descriptions.add(new I18NText(locale, comment));
        task.setDescriptions(descriptions);
        String priority = priorityText.getText();
        priorityText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String priority = priorityText.getText();
                boolean enabled = false;
                if (priority.length() == 0) {
                    enabled = true;
                } else {
                    try {
                        new Integer(priority);
                        enabled = true;
                    } catch (NumberFormatException exc) {
                        // do nothing
                    }
                }
                getButton(IDialogConstants.OK_ID).setEnabled(enabled);

            }
        });
        try {
            task.setPriority(new Integer(priority));
        } catch (NumberFormatException e) {
            // do nothing
        }
        TaskData taskData = new TaskData();
        taskData.setSkipable(skippableButton.getSelection());
        task.setTaskData(taskData);

        String actors = actorText.getText();
        PeopleAssignments assignments = new PeopleAssignments();
        String[] actorIds = actors.trim().split(",");
        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
        for (String id: actorIds) {
            User user = new User();
            user.setId(id.trim());
            potentialOwners.add(user);
        }
        assignments.setPotentialOwners(potentialOwners);

        List<OrganizationalEntity> businessAdministrators = new ArrayList<OrganizationalEntity>();
        businessAdministrators.add(new User("Administrator"));
        assignments.setBusinessAdministrators(businessAdministrators);
        task.setPeopleAssignments(assignments);

        ContentData content = null;
        String contentString = contentText.getText();
        content = new ContentData();
        content.setContent(contentString.getBytes());
        content.setAccessType(AccessType.Inline);
    }

    public Task getTask() {
        return task;
    }

    public ContentData getContent() {
        return content;
    }

}
