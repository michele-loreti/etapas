package org.cmg.tapas;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.cmg.tapas.extensions.TAPAsElementViewProvider;
import org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider;
import org.cmg.tapas.extensions.TAPAsLTSBuilderProvider;
import org.cmg.tapas.extensions.TAPAsModelCheckerProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

public class TAPAsProjectHelper {
	public static final String TAPAS_MAIN = "org.cmg.tapas";
	public static final String TAPAS_CORE = "org.cmg.tapas.core";
	
	public static final String TAPAS_VIEW_PROVIDER_ID = "org.cmg.tapas.TAPAsElementViewProvider";
	public static final String TAPAS_LTS_BUILDER_PROVIDER_ID = "org.cmg.tapas.TAPAsLTSBuilderProvider";
	public static final String TAPAS_EC_PROVIDER_ID = "org.cmg.tapas.TAPAsEquivalenceCheckerProvider";
	public static final String TAPAS_MC_PROVIDER_ID = "org.cmg.tapas.TAPAsModelCheckerProvider";
	
	
	public static Collection<? extends String> addTapasBudles(List<String> bundles ) {
		bundles.add(TAPAS_MAIN);
		bundles.add(TAPAS_CORE);
		return bundles;
	}
	
	public static String getPackageFolder( Resource resource ) {
		URI uri = resource.getURI();
		if (uri.isPlatform()) {
			String toReturn = uri.toPlatformString(true);
			if (toReturn.startsWith("/")) {
				toReturn = toReturn.substring(1);
			}
			int lastSlash = toReturn.lastIndexOf('/');
			String packageFolder;
			if (lastSlash < 0) {
				packageFolder = "";
			} else {
				packageFolder = toReturn.substring(0, lastSlash);

			}
			packageFolder = packageFolder.replace('.', '_');
			int lastDot = toReturn.lastIndexOf('.');
			if (lastDot >= 0) {
				packageFolder = packageFolder+"/"+(toReturn.substring(lastDot+1));
			}
			return packageFolder;
		}
		return "test";
	}
	
	public static String getClassName( Resource resource , String name ) {
		URI uri = resource.getURI();
		if (uri.isPlatform()) {
			String toReturn = uri.toPlatformString(true);
			int start = toReturn.lastIndexOf('/');
			if (start < 0) {
				start = 0;
			} else {
				start = start + 1;
			}
			int end = toReturn.lastIndexOf('.');
			if (end < 0) {
				end = toReturn.length();
			}
			return toReturn.substring(start, end)+"."+name;
		}
		return null;
	}
	
	public static Class<?> loadClassFromProject( ClassLoader parentClassLoader , Resource resource , String name , IProject project ) throws CoreException, MalformedURLException, ClassNotFoundException {
//		ClassLoader cLoader = Thread.currentThread().getContextClassLoader();
		String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(JavaCore.create(project));
//		URL url = JavaCore.create(project).getOutputLocation().toFile().
//		List<URL> urlList = new ArrayList<URL>();
//		for (int i = 0; i < classPathEntries.length; i++) {
		 String entry = classPathEntries[0];
		 IPath path = new Path(entry);
		 URL url = path.toFile().toURI().toURL();
//		 urlList.add(url);
//		}
//		ClassLoader parentClassLoader = project.getClass().getClassLoader();
//		URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
//		URLClassLoader classLoader = new URLClassLoader(urls, cLoader);
		URLClassLoader classLoader = new URLClassLoader(new URL[] { url } , parentClassLoader);

		Class<?> clazz = classLoader.loadClass(TAPAsProjectHelper.getPackageFolder(resource).replace('/','.')+"."+
				name);//TAPAsProjectHelper.getClassName(resource,name));
		return clazz;
	}
	
	public static TAPAsElementViewProvider getElementViewProvider( String extension ) {
		IExtensionRegistry platformRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = platformRegistry.getConfigurationElementsFor(TAPAS_VIEW_PROVIDER_ID);
		for (IConfigurationElement e : elements) {
			if (e.getAttribute("extension").equals(extension)) {
		        Object o;
				try {
					o = e.createExecutableExtension("class");
		            if (o instanceof TAPAsElementViewProvider) {
		            	return (TAPAsElementViewProvider) o;
		            }
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}		
		return null;
	}
	
	public static TAPAsLTSBuilderProvider getLTSBuilderProvider( String extension ) {
		IExtensionRegistry platformRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = platformRegistry.getConfigurationElementsFor(TAPAS_LTS_BUILDER_PROVIDER_ID);
		for (IConfigurationElement e : elements) {
			if (e.getAttribute("extension").equals(extension)) {
		        Object o;
				try {
					o = e.createExecutableExtension("class");
		            if (o instanceof TAPAsLTSBuilderProvider) {
		            	return (TAPAsLTSBuilderProvider) o;
		            }
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}		
		return null;
	}
	
	public static TAPAsEquivalenceCheckerProvider getEquivalenceCheckerProvider( String extension ) {
		IExtensionRegistry platformRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = platformRegistry.getConfigurationElementsFor(TAPAS_EC_PROVIDER_ID);
		for (IConfigurationElement e : elements) {
			if (e.getAttribute("extension").equals(extension)) {
		        Object o;
				try {
					o = e.createExecutableExtension("class");
		            if (o instanceof TAPAsEquivalenceCheckerProvider) {
		            	return (TAPAsEquivalenceCheckerProvider) o;
		            }
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}		
		return null;
	}
	
	public static TAPAsModelCheckerProvider getModelCheckerProvider( String extension ) {
		IExtensionRegistry platformRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = platformRegistry.getConfigurationElementsFor(TAPAS_MC_PROVIDER_ID);
		for (IConfigurationElement e : elements) {
			if (e.getAttribute("extension").equals(extension)) {
		        Object o;
				try {
					o = e.createExecutableExtension("class");
		            if (o instanceof TAPAsModelCheckerProvider) {
		            	return (TAPAsModelCheckerProvider) o;
		            }
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}		
		return null;
	}
	
	public static EObject getModel(XtextEditor editor) {
		if (editor instanceof XtextEditor) {
			XtextEditor xEditor = (XtextEditor) editor;
	
			EList<EObject> values = xEditor.getDocument().readOnly(new IUnitOfWork<EList<EObject>, XtextResource>(){
	
				@Override
				public EList<EObject> exec(XtextResource state) throws Exception {
					if (state.getErrors().size()>0) {
						return null;
					}
					return state.getContents();
				}
				
			});		
			if ((values != null)&&(values.size() > 0)) {
				return values.get(0);
			}
		}
		return null;
	}
}
