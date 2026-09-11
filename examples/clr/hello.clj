(ns hello.clr
  (:import [System String DateTimeOffset]
           [System.Globalization CultureInfo]))

(defn greeting [name]
  (String/Format "Hello, {0} from ClojureCLR!"
                 (if (String/IsNullOrWhiteSpace name) "world" name)))

(defn receipt [name]
  (str (greeting name) " UTC: "
       (.ToString DateTimeOffset/UtcNow "O" CultureInfo/InvariantCulture)))
