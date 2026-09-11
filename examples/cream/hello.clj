(definterface Greeting
  (greet [name]))

(deftype Greeter [prefix]
  Greeting
  (greet [_ name] (str prefix ", " name "!")))

(println (.greet (Greeter. "Hello")
                 (or (first *command-line-args*) "world")))
