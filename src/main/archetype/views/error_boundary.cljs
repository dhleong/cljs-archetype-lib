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

(defn- error-view [props err-atom info-atom error]
  (let [{:keys [clean-error clean-component-stack]
         :or {clean-component-stack identity}} props]
    [:div.error-boundary
     [:div.error-boundary-title "Oops! Something went wrong"]
     [:div [:a {:href "#"
                :on-click (fn-click
                            (reset! info-atom nil)
                            (reset! err-atom nil))}
            "Try again"]]

     (when-let [info @info-atom]
       [:pre "Component Stack:\n"
        (clean-component-stack
          (.-componentStack info))])

     [:pre "Error:\n"
      (cond
        clean-error (clean-error error)
        (ex-message error) (.-stack error)
        :else (str error))]]))

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
                         (let [props (when (map? (first children))
                                       (first children))
                               children (if (map? (first children))
                                          (rest children)
                                          children)]
                           (if-let [e @err]
                             [error-view props err info-atom e]

                             (into [:<>] children))))})))

