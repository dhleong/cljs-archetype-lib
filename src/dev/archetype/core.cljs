(ns archetype.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [archetype.views :as views]))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (mount-root))

