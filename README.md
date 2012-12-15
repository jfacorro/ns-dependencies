ns-dependencies
===============

Tool for loading and visualizing dependencies between [Clojure](http://clojure.org) namespaces in any given project.

## Info

There are currently two namespaces `ns-dependencies.load` and `ns-dependencies-view`.

### ns-dependencies.load

The first one is used through the `load-deps` function which receives a path and returns a map with all the namespace declarations found in each of the clojure (*.clj) files contained under the specified path.

The tool assumes the `ns` form in each file is the first one, if the first form is not a namespace declaration then the loading process dismisses the file.

### ns-dependencies.view

The `view-deps` function takes a namespace dependencies map, obtained through `load-deps`, and returns a `JPanel` where the visualization is drawn, this panel can then be added to a `JFrame` to view it on the screen.

## Usage

It's a lot easier to run the example if you have [leiningen](https://github.com/technomancy/leiningen) installed.

	$ git clone https://github.com/jfacorro/ns-dependencies.git
	$ cd ns-dependencies

The following will show the dependencies view for the current directory which is the `ns-dependencies` project.

	$ lein run
	
If you want to specify the directory for another project just add it as an argument.

	$ lein run path-to-project

## License

Copyright © 2012 Juan Facorro

Distributed under the Eclipse Public License, the same as Clojure.
