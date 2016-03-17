JavaScript Build Eclipse
=========

[![Build Status](https://secure.travis-ci.org/angelozerr/jsbuild-eclipse.png)](http://travis-ci.org/angelozerr/jsbuild-eclipse)
[![Eclipse install](https://marketplace.eclipse.org/sites/all/modules/custom/marketplace/images/installbutton.png)](http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=2295381)

**Since Eclipse Neon will provide Gulp/Grunt support, I have decided to give up this project.** See https://wiki.eclipse.org/JSDT/JSDT_Neon_Plan and demo at https://www.youtube.com/watch?v=V7sZVTNHNYM

`JavaScript Build Eclipse` is a set of plugins based on [tern.java](https://github.com/angelozerr/tern.java) which gives support for [Grunt](http://gruntjs.com/) and [Gulp](http://gulpjs.com/) (and more?). It provides the `Build Explorer` view which looks like the `Ant` view. The `Build Explorer` displays in a tree the tasks : 

![Build file View](https://github.com/angelozerr/jsbuild-eclipse/wiki/images/BuildFileView_Overview.png)

With the `Build Explorer`, you can :

 * `display in a tree, tasks and targets` from `Gruntfile.js` / `glupfile.js`.
 * `execute task/target` with Eclipse launch by double clicking in the task / target item of the tree.
 * `navigate to the definition of a task or target`: opens the Gruntfile.js / glupfile.js and selects the location where task/target is declared. 

To collect tasks / targets from `Gruntfile.js` / `glupfile.js`, [tern.java](https://github.com/angelozerr/tern.java) is used : 

 * [tern-grunt](https://github.com/angelozerr/tern-grunt) : a tern plugin which adding support for [Grunt](http://gruntjs.com/).
 * [tern-gulp](https://github.com/angelozerr/tern-gulp) : a tern plugin which adding support for [Gulp](http://gulpjs.com/).
 
Those tern plugins are used too for JavaScript editor completion : 

![grunt completion](https://github.com/angelozerr/jsbuild-eclipse/wiki/images/GruntCompletion.png)
 
# Installation

`JavaScript Build Eclipse` Eclipse is developed/tested with Eclipse 4.4 Luna. It is advised to use Eclipse 4.4 Luna (even if AngularJS Eclipse could work with older version of Eclipse) : 

 * if you start with `JavaScript Build Eclipse`, please read [Getting Started](https://github.com/angelozerr/jsbuild-eclipse/wiki/Getting-Started).
 * to install it, please read [Installation - Update Site](https://github.com/angelozerr/jsbuild-eclipse/wiki/Installation-Update-Site) section.

# Build

`JavaScript Build Eclipse` Eclipse is build with this [cloudbees job](https://opensagres.ci.cloudbees.com/job/jsbuild-eclipse/).

# Development

tern.java is used to load tasks from Gruntfile.js / gulpfile.js but **JavaScript Build Eclipse** is not linked to tern.java. You could develop a plugin which load tasks and after benefit with the Build explorer view, launch. Evry plugins which starts with  fr.opensagres.eclipse.jsbuild.* are not linked to tern.java.
