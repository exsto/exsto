(ns exsto.events
  (:require [re-frame.core :as rf]
            [exsto.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-fx
 :button-click
 (fn [cofx _]
   {:dispatch [:add-item :drag-button]}))

(rf/reg-event-fx
 :drop-target
 (fn [cofx _]
   (let [db (:db cofx)]
     {:dispatch [:add-item (get-in db [:draggable :type])]})))

(rf/reg-event-db
 :create-target
 (fn [db [_ ox oy x y type]]
   (-> db
       (assoc-in [:draggable :pos] {:x (- x ox 1) :y (- y oy 1)})
       (assoc-in [:draggable :created?] true)
       (assoc-in [:draggable :type] type))))

(rf/reg-event-db
 :remove-target
 (fn [db _]
   (assoc-in db [:draggable :created?] false)))

(rf/reg-event-db
 :move-target
 (fn [db [_ x y]]
   (-> db
       (assoc-in [:draggable :pos :x] (- x (get-in db [:draggable :pos :ox]) 1))
       (assoc-in [:draggable :pos :y] (- y (get-in db [:draggable :pos :oy]) 1)))))

(rf/reg-event-db
 :grab-target
 (fn [db [_  ox oy]]
   (-> db
       (assoc-in [:draggable :pos :ox] ox)
       (assoc-in [:draggable :pos :oy] oy)
       (assoc-in [:draggable :can-move?] true))))

(rf/reg-event-db
 :release-target
 (fn [db _]
   (assoc-in db [:draggable :can-move?] false)))

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
