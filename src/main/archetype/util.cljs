(ns archetype.util
  (:require [re-frame.core :refer [subscribe dispatch]]))

(def <sub (comp deref subscribe))
(def >evt dispatch)

