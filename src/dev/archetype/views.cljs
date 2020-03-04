(ns archetype.views
  (:require [archetype.views.error-boundary :refer [error-boundary]]))

(defn title []
  [:div "archetype"])

(defn error []
  (throw (ex-info {} "Error!")))

(defn main []
  [:div
   [title]

   [:div "Error boundary:"]
   [error-boundary
    [error]]])
