(ns exsto.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:.dropzone {:min-height "60vh"
               :margin-bottom "1em"
               :background "#e7e7e7"
               :border-radius "10px"}])
