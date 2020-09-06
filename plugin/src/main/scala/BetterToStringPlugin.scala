package com.kubukoz

import scala.tools.nsc.plugins.{Plugin, PluginComponent}
import scala.tools.nsc.{Global, Phase}
import scala.tools.nsc.transform.TypingTransformers

class BetterToStringPlugin(override val global: Global) extends Plugin {
  override val name: String = "better-tostring"
  override val description: String = "scala compiler plugin for better default toString implementations"
  override val components: List[PluginComponent] = List(new BetterToStringPluginComponent(global))
}

class BetterToStringPluginComponent(val global: Global) extends PluginComponent with TypingTransformers {
  import global._
  override val phaseName: String = "better-tostring-phase"
  override val runsAfter: List[String] = List("parser")

  private def modifyClasses(tree: Tree): Tree = tree match {
    case p: PackageDef => p.copy(stats = p.stats.map(modifyClasses))
    case m: ModuleDef =>
      if (m.name.toString() == "Demo")
        //this breaks things
        m.copy()
      else m
    case other => other
  }

  override def newPhase(prev: Phase): Phase = new StdPhase(prev) {

    override def apply(unit: CompilationUnit): Unit =
      new Transformer {
        override def transform(tree: Tree): Tree = modifyClasses(tree)
      }.transformUnit(unit)
  }
}
