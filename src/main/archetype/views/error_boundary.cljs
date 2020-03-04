(ns archetype.views.error-boundary
  (:require [reagent.core :as r]
            [archetype.util :refer-macros [fn-click]]))

(when goog.DEBUG
  ; in debug builds, we can auto-retry rendering error'd components
  ; every time a load occurs
  (def ^:private active-err-atoms (atom #{}))

  (defn- ^:dev/after-load clear-errors []
    (swap! active-err-atoms (fn [atoms]
                              (doseq [a atoms]
                                (reset! a nil))

                              ; clear
                              #{}))))

(defn- error-view [err-atom info-atom error]
  [:div.error-boundary
   [:div.error-boundary-title "Oops! Something went wrong"]
   [:div [:a {:href "#"
              :on-click (fn-click
                          (reset! info-atom nil)
                          (reset! err-atom nil))}
          "Try again"]]

   (when-let [info @info-atom]
     [:pre "Component Stack:\n" (.-componentStack info)])

   [:pre (if (ex-message error)
           (.-stack error)
           (str error))]])

(defn error-boundary [& _]
  (r/with-let [err (r/atom nil)
               info-atom (r/atom nil)]
    (r/create-class
      {:display-name "Error Boundary"

       :component-did-catch (fn [_this error info]
                              (js/console.warn error info)
                              (reset! err error)
                              (reset! info-atom info))

       :statics
       #js {:getDerivedStateFromError (fn [error]
                                        (when goog.DEBUG
                                          ; enqueue the atom for auto-clearing
                                          (swap! active-err-atoms conj err))

                                        (reset! err error))}

       :reagent-render (fn [& children]
                         (if-let [e @err]
                           [error-view err info-atom e]

                           (into [:<>] children)))})))

