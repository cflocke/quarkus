package org.jboss.resteasy.reactive.server.processor.generation.filters;

import static org.jboss.resteasy.reactive.server.processor.util.ResteasyReactiveServerDotNames.SERVER_REQUEST_FILTER;
import static org.jboss.resteasy.reactive.server.processor.util.ResteasyReactiveServerDotNames.SERVER_RESPONSE_FILTER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.resteasy.reactive.common.processor.ResteasyReactiveDotNames;
import org.jboss.resteasy.reactive.server.processor.util.GeneratedClass;
import org.jboss.resteasy.reactive.server.processor.util.GeneratedClassOutput;

public class FilterGeneration {
    public static List<GeneratedFilter> generate(IndexView index, Set<DotName> unwrappableTypes,
            Set<String> additionalBeanAnnotations) {
        List<GeneratedFilter> ret = new ArrayList<>();
        for (AnnotationInstance instance : index
                .getAnnotations(SERVER_REQUEST_FILTER)) {
            if (instance.target().kind() != AnnotationTarget.Kind.METHOD) {
                continue;
            }
            MethodInfo methodInfo = instance.target().asMethod();
            GeneratedClassOutput output = new GeneratedClassOutput();
            String generatedClassName = CustomFilterGenerator.generateContainerRequestFilter(methodInfo, output,
                    unwrappableTypes, additionalBeanAnnotations);
            Integer priority = null;
            boolean preMatching = false;
            boolean nonBlockingRequired = false;
            Set<String> nameBindingNames = new HashSet<>();

            AnnotationValue priorityValue = instance.value("priority");
            if (priorityValue != null) {
                priority = priorityValue.asInt();
            }
            AnnotationValue preMatchingValue = instance.value("preMatching");
            if (preMatchingValue != null) {
                preMatching = preMatchingValue.asBoolean();
            }
            AnnotationValue nonBlockingRequiredValue = instance.value("nonBlocking");
            if (nonBlockingRequiredValue != null) {
                nonBlockingRequired = nonBlockingRequiredValue.asBoolean();
            }

            List<AnnotationInstance> annotations = methodInfo.annotations();
            for (AnnotationInstance annotation : annotations) {
                if (SERVER_REQUEST_FILTER.equals(annotation.name())) {
                    continue;
                }
                DotName annotationDotName = annotation.name();
                ClassInfo annotationClassInfo = index.getClassByName(annotationDotName);
                if (annotationClassInfo == null) {
                    continue;
                }
                if ((annotationClassInfo.classAnnotation(ResteasyReactiveDotNames.NAME_BINDING) != null)) {
                    nameBindingNames.add(annotationDotName.toString());
                }
            }

            ret.add(new GeneratedFilter(output.getOutput(), generatedClassName, methodInfo.declaringClass().name().toString(),
                    true, priority, preMatching, nonBlockingRequired, nameBindingNames));
        }
        for (AnnotationInstance instance : index
                .getAnnotations(SERVER_RESPONSE_FILTER)) {
            if (instance.target().kind() != AnnotationTarget.Kind.METHOD) {
                continue;
            }
            MethodInfo methodInfo = instance.target().asMethod();
            Integer priority = null;
            Set<String> nameBindingNames = new HashSet<>();
            GeneratedClassOutput output = new GeneratedClassOutput();
            String generatedClassName = CustomFilterGenerator.generateContainerResponseFilter(methodInfo, output,
                    unwrappableTypes, additionalBeanAnnotations);

            AnnotationValue priorityValue = instance.value("priority");
            if (priorityValue != null) {
                priority = priorityValue.asInt();
            }
            List<AnnotationInstance> annotations = methodInfo.annotations();
            for (AnnotationInstance annotation : annotations) {
                if (SERVER_REQUEST_FILTER.equals(annotation.name())) {
                    continue;
                }
                DotName annotationDotName = annotation.name();
                ClassInfo annotationClassInfo = index.getClassByName(annotationDotName);
                if (annotationClassInfo == null) {
                    continue;
                }
                if ((annotationClassInfo.classAnnotation(ResteasyReactiveDotNames.NAME_BINDING) != null)) {
                    nameBindingNames.add(annotationDotName.toString());
                }
            }

            ret.add(new GeneratedFilter(output.getOutput(), generatedClassName, methodInfo.declaringClass().name().toString(),
                    false, priority, false, false, nameBindingNames));

        }
        return ret;
    }

    public static class GeneratedFilter {
        final List<GeneratedClass> generatedClasses;
        final String generatedClassName;
        final String declaringClassName;
        final boolean requestFilter;
        final Integer priority;
        final boolean preMatching;
        final boolean nonBlocking;
        final Set<String> nameBindingNames;

        public GeneratedFilter(List<GeneratedClass> generatedClasses, String generatedClassName, String declaringClassName,
                boolean requestFilter, Integer priority, boolean preMatching, boolean nonBlocking,
                Set<String> nameBindingNames) {
            this.generatedClasses = generatedClasses;
            this.generatedClassName = generatedClassName;
            this.declaringClassName = declaringClassName;
            this.requestFilter = requestFilter;
            this.priority = priority;
            this.preMatching = preMatching;
            this.nonBlocking = nonBlocking;
            this.nameBindingNames = nameBindingNames;
        }

        public String getGeneratedClassName() {
            return generatedClassName;
        }

        public String getDeclaringClassName() {
            return declaringClassName;
        }

        public boolean isRequestFilter() {
            return requestFilter;
        }

        public Integer getPriority() {
            return priority;
        }

        public boolean isPreMatching() {
            return preMatching;
        }

        public boolean isNonBlocking() {
            return nonBlocking;
        }

        public List<GeneratedClass> getGeneratedClasses() {
            return generatedClasses;
        }

        public Set<String> getNameBindingNames() {
            return nameBindingNames;
        }
    }

}
