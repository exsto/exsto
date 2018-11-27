(ns exsto.events
  (:require [re-frame.core :as rf]
            [exsto.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-fx
 :button-click
 (fn [cofx [_ _]]
   {:dispatch [:add-item :drag-button]}))

(defn- get-new-input-id [form-inputs]
  (let [ids (map :id form-inputs)]
    (if (empty? ids)
      0
      (inc (apply max ids)))))

(rf/reg-event-db
 :add-item
 (fn [db [_ input-type]]
   (let [new-id (get-new-input-id (db :form-inputs))]
     (update db :form-inputs conj {:id new-id
                                   :type input-type
                                   :props {}}))))
