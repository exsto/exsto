(ns exsto.views
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-com.core :as rc]
            [exsto.subs :as subs]))

(defn title []
  (let [name @(rf/subscribe [::subs/name])]
    [rc/title
     :label (str name " - web form constructor")
     :level :level2]))

(defn counter []
  (let [count @(rf/subscribe [::subs/count])]
    [rc/p [:strong "number of forms: "] count]))

(defn button []
  [rc/button
   :label "Add dummy item"
   :on-click #(rf/dispatch [:button-click])])

(defn header [] [title])

(defn allow-drop [e]
  (.preventDefault e))

(defn handle-drag [e]
  (-> e
      .-dataTransfer
      (.setData "text"
                (-> e .-target .-id))))

(defn handle-drop [e]
  (.preventDefault e)
  (let [data (keyword (-> e .-dataTransfer (.getData "text")))]
    (.log js/console (str "drop data " data ))
    (rf/dispatch [:add-item data])))

(def draggable
  {:draggable     true
   :on-drag-start handle-drag})

(def dropable
  {:on-drop       handle-drop
   :on-drag-over  allow-drop})

(defn drag-checkbox [atom]
  [rc/checkbox
   :model @atom
   :on-change #(reset! atom %)])

(defn drag-button []
  [rc/button :label "button"])

(defn drag-input [atom]
  [:input
   {:type "text"
    :value @atom
    :auto-focus true
    :on-change #(reset! atom (-> % .-target .-value))}])

(def forms-col
  {:drag-checkbox [drag-checkbox (r/atom true)]
   :drag-button   [drag-button]
   :drag-input    [drag-input    (r/atom "input value")]})

(defn nav []
  (let [input-val (r/atom "input value")
        cb        (r/atom true)]
    (fn []
      [:ul
       [:li
        (assoc draggable :id "drag-input")
        [drag-input input-val]]
       [:li
        (assoc draggable :id "drag-button")
        [drag-button]]
       [:li
        (assoc draggable :id "drag-checkbox")
        [drag-checkbox cb]]])))

(defn dropzone [e]
  (let [items (rf/subscribe [::subs/items])]
    (fn []
      (.log js/console (str @items))
      [:div.dropzone
       dropable
       [:ul
        (for [form @items]
          ^{:key (:id form)} [:li (forms-col form)])]])))

(defn content []
  [:div
   [dropzone]
   [counter]
   [button]])

(defn main-panel []
  [rc/v-box
   :children [[rc/box :child [header]]
              [rc/h-box
               :height "80vh"
               :children [[rc/box :size "20vw" :child [nav]]
                          [rc/box :size "1"    :child [content]]]]]])
