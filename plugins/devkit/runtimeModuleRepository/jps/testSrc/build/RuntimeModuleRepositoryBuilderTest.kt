// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.devkit.runtimeModuleRepository.jps.build

import org.jetbrains.jps.model.java.JavaResourceRootType
import org.jetbrains.jps.model.java.JpsJavaDependencyScope
import org.jetbrains.jps.model.java.JpsJavaExtensionService

class RuntimeModuleRepositoryBuilderTest : RuntimeModuleRepositoryTestCase() {
  fun `test module with tests`() {
    addModule("a", withTests = true)
    buildAndCheck { 
      descriptor("a")
      testDescriptor("a.tests", "a")
    }
  }
  
  fun `test module without sources`() {
    addModule("a", withTests = false, withSources = false)
    buildAndCheck { 
      descriptor("a", resourceDirName = null)
    }
  }
  
  fun `test module with resources only`() {
    val module = addModule("a", withTests = false, withSources = false)
    module.addSourceRoot(getUrl("a/res"), JavaResourceRootType.RESOURCE)
    buildAndCheck { 
      descriptor("a")
    }
  }

  fun `test dependency`() {
    val a = addModule("a", withTests = false)
    addModule("b", a, withTests = false)
    buildAndCheck { 
      descriptor("a")
      descriptor("b", "a")
    }
  }
  
  fun `test transitive dependency`() {
    val a = addModule("a", withTests = false)
    val b = addModule("b", a, withTests = false)
    addModule("c", b, withTests = false)
    buildAndCheck { 
      descriptor("a")
      descriptor("b", "a")
      descriptor("c", "b")
    }
  }
  
  fun `test dependency with tests`() {
    val a = addModule("a", withTests = true)
    addModule("b", a, withTests = true)
    buildAndCheck { 
      descriptor("a")
      testDescriptor("a.tests", "a")
      descriptor("b", "a")
      testDescriptor("b.tests", "b", "a.tests")
    }
  }

  fun `test transitive dependency via module without tests`() {
    val a = addModule("a", withTests = true)
    val b = addModule("b", a, withTests = false)
    addModule("c", b, withTests = true)
    buildAndCheck {
      descriptor("a")
      descriptor("b", "a")
      descriptor("c", "b")
      testDescriptor("a.tests", "a")
      testDescriptor("c.tests", "c", "b", "a.tests")
    }
  }

  fun `test circular dependency with tests`() {
    val a = addModule("a", withTests = true)
    val b = addModule("b", a, withTests = true)
    val dependency = a.dependenciesList.addModuleDependency(b)
    JpsJavaExtensionService.getInstance().getOrCreateDependencyExtension(dependency).scope = JpsJavaDependencyScope.RUNTIME
    buildAndCheck {
      descriptor("a", "b")
      testDescriptor("a.tests", "a", "b.tests")
      descriptor("b", "a")
      testDescriptor("b.tests", "b", "a.tests")
    }
  }
  
  fun `test circular dependency without tests`() {
    val a = addModule("a", withTests = false)
    val b = addModule("b", a, withTests = false)
    val dependency = a.dependenciesList.addModuleDependency(b)
    JpsJavaExtensionService.getInstance().getOrCreateDependencyExtension(dependency).scope = JpsJavaDependencyScope.RUNTIME
    addModule("c", b, withTests = true)
    buildAndCheck {
      descriptor("a", "b")
      descriptor("b", "a")
      descriptor("c", "b")
      testDescriptor("c.tests", "c", "b", "a")
    }
  }

  fun `test separate module for tests`() {
    addModule("a", withTests = false)
    addModule("a.tests", withTests = true, withSources = false)
    buildAndCheck {
      descriptor("a")
      testDescriptor("a.tests", "a", resourceDirName = "a.tests")
    }
  }
}

