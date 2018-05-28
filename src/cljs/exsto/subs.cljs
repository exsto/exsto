(ns exsto.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::name
 (fn [db] (:name db)))

(rf/reg-sub
 ::count
 (fn [db] (count (:items db))))

(rf/reg-sub
 ::items
 (fn [db] (:items db)))
