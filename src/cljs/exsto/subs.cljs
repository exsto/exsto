(ns exsto.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::count
 (fn [db] (count (:form-inputs db))))

(rf/reg-sub
 ::form-inputs
 (fn [db] (:form-inputs db)))
