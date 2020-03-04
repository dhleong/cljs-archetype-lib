(ns archetype.nav
  (:require [clojure.string :as str]
            [re-frame.core :refer [dispatch]]
            [secretary.core :as secretary]
            [goog.events :as gevents]
            [goog.history.EventType :as HistoryEventType]
            [pushy.core :as pushy])
  (:import goog.History))

(def ^:private pushy-supported? (pushy/supported?))

(def ^:private pushy-prefix "/")
(def ^:private secretary-prefix (if pushy-supported?
                                  (str pushy-prefix "/")
                                  "#"))

(defn init! []
  (secretary/set-config! :prefix secretary-prefix))

;; from secretary
(defn- uri-without-prefix [uri]
  (str/replace uri (re-pattern (str "^" secretary-prefix)) ""))
(defn- uri-with-leading-slash
  "Ensures that the uri has a leading slash"
  [uri]
  (if (= "/" (first uri))
    uri
    (str "/" uri)))

;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (if pushy-supported?
    ; fancy html5 navigation
    (let [history (pushy/pushy
                    secretary/dispatch!
                    (fn [x]
                      (let [[uri-path _query-string]
                            (str/split (uri-without-prefix x) #"\?")
                            uri-path (uri-with-leading-slash uri-path)]
                        (when (secretary/locate-route uri-path)
                          x))))]
      (pushy/start! history))

    ; #-based navigation
    (doto (History.)
      (gevents/listen
        HistoryEventType/NAVIGATE
        (fn [event]
          (secretary/dispatch! (.-token event))))
      (.setEnabled true))))

(defn prefix
  "Prefix a link as necessary for :href-based navigation to work"
  [raw-link]
  (if pushy-supported?
    (str pushy-prefix raw-link)
    (str "#" raw-link)))

(defn navigate! [& args]
  (dispatch (into [:navigate!] args)))

(defn replace!
  "Wrapper around js/window.location.replace"
  [new-location]
  (js/window.location.replace
    (prefix new-location)))


