(ns hello.core
  (:require [clojure.string :as str]
            [replicant.dom :as r]))

(defonce state (atom {:name "" :greetings [] :page :home}))

(defn greet [s]
  (update s :greetings conj
          (str "Hello, " (if (str/blank? (:name s)) "world" (str/trim (:name s))) "!")))

(defn view [{:keys [name greetings page]}]
  [:main
   [:header [:p.eyebrow "CLOJURE DIALECTS / 01"]
    [:h1 "Hello, browser."]
    [:p "A tiny single-page app, built from data."]]
   [:nav {:aria-label "Pages"}
    [:a {:href "#/" :aria-current (when (= page :home) "page")} "Say hello"]
    [:a {:href "#/history" :aria-current (when (= page :history) "page")}
     (str "Greetings (" (count greetings) ")")]]
   (if (= page :history)
     [:section [:h2 "Your greetings"]
      (if (seq greetings)
        [:ol (map-indexed (fn [i greeting] [:li {:replicant/key i} greeting]) greetings)]
        [:p "No greetings yet. Say hello to get started."])
      [:button {:on {:click #(swap! state assoc :greetings [])}
                :disabled (empty? greetings)} "Clear greetings"]]
     [:section
      [:form {:on {:submit (fn [e] (.preventDefault e) (swap! state greet))}}
       [:label {:for "name"} "What's your name?"]
       [:input {:id "name" :name "name" :value name :placeholder "World"
                :autocomplete "given-name" :maxlength 100
                :on {:input #(swap! state assoc :name (.. % -target -value))}}]
       [:button {:type "submit"} "Say hello"]]
      [:p.greeting {:role "status" :aria-live "polite"}
       (or (peek greetings) "Hello, world!")]])
   [:footer "ClojureScript + Replicant · state → hiccup → DOM"]])

(defn render! []
  (r/render (.getElementById js/document "app") (view @state)))

(defn route! []
  (swap! state assoc :page (if (= "#/history" (.-hash js/location)) :history :home)))

(defn ^:export init []
  (add-watch state ::render (fn [_ _ _ _] (render!)))
  (.addEventListener js/window "hashchange" route!)
  (route!))

(init)
