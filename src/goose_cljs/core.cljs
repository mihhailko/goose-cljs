(ns goose-cljs.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST PUT DELETE]]))

(enable-console-print!)

(defonce app-state (atom {:birds []
                          :current-bird 0
                          :current-bird-content "nothing yet"
                          :selector 0
                          :selector-content nil
                          :chosen-bird nil}))


(defn get-name [] (get (:current-bird-content @app-state) "bird_name"))

(defn get-description [] (get (:current-bird-content @app-state) "bird_description"))

(defn bird-data []
  [:div
   [:h3 "You are ready to select:"]
   [:h1 (get-name)]
   [:h3 (get-description)]])

(defn load-birds [stuff]
  (do
    (swap! app-state assoc :birds stuff :current-bird 0)
    (swap! app-state assoc :current-bird-content (nth (:birds @app-state) (:current-bird @app-state)))))

(defn next-bird []
  (do
    (swap! app-state assoc :current-bird (inc (:current-bird @app-state)))
    (swap! app-state assoc :current-bird-content (nth (:birds @app-state) (:current-bird @app-state)))))

(defn prev-bird []
  (do
    (swap! app-state assoc :current-bird (dec (:current-bird @app-state)))
    (swap! app-state assoc :current-bird-content (nth (:birds @app-state) (:current-bird @app-state)))))

(defn nav-buttons []
  [:div
   [:button {:type "input"
             :class "button"
             :on-click #(next-bird)}
    "Next bird"]
   [:p]
   [:button {:type "input"
             :class "button"
             :on-click #(prev-bird)}
    "Previous bird"]])
              
(defn handler [response]
  (load-birds (js->clj response)))

(defn error-handler [response]
  (.log js/console (str response)))

(defn flying-request []
  (GET "http://localhost:8080/1" {:handler handler
                                  :error-handler error-handler}))

(defn non-flying-request []
  (GET "http://localhost:8080/2" {:handler handler
                                  :error-handler error-handler}))

(defn it-depends-request []
  (GET "http://localhost:8080/3" {:handler handler
                                  :error-handler error-handler}))


(defn flying-request-button []
  [:div
   [:button {:type "input"
             :class "button"
             :on-click #(flying-request)}
    "Load flying birds"]])

(defn non-flying-request-button []
  [:div
   [:button {:type "input"
             :class "button"
             :on-click #(non-flying-request)}
    "Load non-flying birds"]])

(defn it-depends-request-button []
  [:div
   [:button {:type "input"
             :class "button"
             :on-click #(it-depends-request)}
    "Load weird birds"]])

(def content-vec [[:div
                   [:h3 "Choose your goose"]
                   [:h2 "Not a joke"]
                   [:h3 "It is all about companionship"]]
                  [:div
                   [:h3 "Flying birds"]
                   [flying-request-button]]
                  [:div
                   [:h3 "Non-flying birds"]
                   [non-flying-request-button]]
                  [:div
                   [:h3 "Flying? Well, it depends..."]
                   [it-depends-request-button]]
                  [:div
                   [:h2 "The Chosen One"]
                   [:h3 (str "Selected bird's id: " (:chosen-bird @app-state))]]
                  [:div
                   [:h3 "Add your goose"]]])

(defn next-widget []
  (if (<= (:selector @app-state) (- (count content-vec) 2))
    (do
      (swap! app-state assoc :selector (inc (:selector @app-state)))
      (swap! app-state assoc :selector-content (nth content-vec (:selector @app-state))))
    nil))

(defn prev-widget []
  (if (>= (:selector @app-state) 1)
    (do
      (swap! app-state assoc :selector (dec (:selector @app-state)))
      (swap! app-state assoc :selector-content (nth content-vec (:selector @app-state))))
    nil))

(defn global-nav-buttons []
  [:div
   [:button {:type "input"
             :class "button"
             :on-click #(next-widget)}
    "Forward"]
   [:button {:type "input"
             :class "button"
             :on-click #(prev-widget)}
    "Back"]])

(defn current-component []
  (if (= 0 (:selector @app-state))
    (nth content-vec 0)
    (:selector-content @app-state)))

(defn hello-world []
  [:div
   [global-nav-buttons]
   [current-component]
   [bird-data]
   [nav-buttons]
   ])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

