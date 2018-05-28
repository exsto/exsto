(ns exsto.events
  (:require [re-frame.core :as rf]
            [exsto.db :as db]
            ))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-db
 :button-click
 (fn [db _]
   (update db :items conj :drag-button)))

(rf/reg-event-db
 :add-item
 (fn [db [_ item]]
   (update db :items conj item)))
