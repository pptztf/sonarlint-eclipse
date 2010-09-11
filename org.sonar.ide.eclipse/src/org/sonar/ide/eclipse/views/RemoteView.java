package org.sonar.ide.eclipse.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.sonar.ide.api.SourceCode;
import org.sonar.ide.eclipse.internal.EclipseSonar;
import org.sonar.ide.eclipse.properties.ProjectProperties;
import org.sonar.ide.eclipse.ui.AbstractPackageExplorerListener;

/**
 * @author Evgeny Mandrikov
 */
public class RemoteView extends ViewPart {

  public static final String ID = "org.sonar.ide.eclipse.views.RemoteView";

  private Browser browser;

  @Override
  public void createPartControl(Composite parent) {
    browser = new Browser(parent, SWT.NONE);
    clear();
  }

  @Override
  public void setFocus() {
    browser.setFocus();
  }

  @Override
  public void init(IViewSite site) throws PartInitException {
    selectionListener.init(site);
    super.init(site);
  }

  @Override
  public void dispose() {
    super.dispose();
    selectionListener.dispose(getViewSite());
  }

  private void clear() {
    browser.setText("Select Java project, package or class in Package Explorer.");
  }

  private AbstractPackageExplorerListener selectionListener = new AbstractPackageExplorerListener(RemoteView.this) {
    @Override
    public void handleSlection(ISelection selection) {
      if (selection instanceof IStructuredSelection) {
        IStructuredSelection sel = (IStructuredSelection) selection;
        Object o = sel.getFirstElement();
        if (o == null) {
          // no selection
          return;
        }

        // TODO SONARIDE-101
        if (o instanceof IJavaProject || o instanceof IPackageFragment || o instanceof ICompilationUnit) {
          IJavaElement javaElement = (IJavaElement) o;
          IResource resource = javaElement.getResource();
          IProject project = resource.getProject();
          updateBrowser(project, resource);
        } else {
          clear();
        }
      }
    }

    private void updateBrowser(IProject project, IResource resource) {
      SourceCode sourceCode = EclipseSonar.getInstance(resource.getProject()).search(resource);
      if (sourceCode == null) {
        browser.setText("Not found.");
        return;
      }
      ProjectProperties properties = ProjectProperties.getInstance(resource);
      browser.setUrl(properties.getUrl() + "/resource/index/" + sourceCode.getKey() + "?metric=coverage");
    }
  };

}