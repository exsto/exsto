(ns exsto.views
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [exsto.subs :as subs]))

(defn counter []
  (let [count @(rf/subscribe [::subs/count])]
    [:p [:strong "number of forms: "] count]))

(defn button []
  [:button.btn.btn-primary
   {:type "button"
    :on-click #(rf/dispatch [:button-click])}
   "Add dummy item"])

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
    (.log js/console (str "drop data " data))
    (rf/dispatch [:add-item data])))

(def draggable
  {:draggable     true
   :on-drag-start handle-drag})

(def dropable
  {:on-drop       handle-drop
   :on-drag-over  allow-drop})

(defn drag-checkbox [atom]
  [:input {:type "checkbox"
           :value @atom
           :on-change #(reset! atom %)}])

(defn drag-button []
  [:button.btn.btn-primary {:type "button"} "button"])

(defn drag-input [atom]
  [:input
   {:type "text"
    :value @atom
    :auto-focus true
    :on-change #(reset! atom (-> % .-target .-value))}])

(def form-components
  {:drag-checkbox [drag-checkbox (r/atom true)]
   :drag-button   [drag-button]
   :drag-input    [drag-input    (r/atom "input value")]})

(defn resolve-form [form]
  (get form-components
       (:type form)))

;; (defn get-input [id]
;;   (let [inputs (rf/subscribe [::subs/form-inputs])]
;;     (first
;;      (filter #(= id (% :id)) inputs))))

(defn toolbox []
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
  (let [inputs (rf/subscribe [::subs/form-inputs])]
    (fn []
      (.log js/console (str @inputs))
      [:div.dropzone dropable
       [:ul (for [form @inputs]
              ^{:key (str "item-" (:id form))}
              [:li (resolve-form form)])]])))

(defn constructor []
  [:div
   [dropzone]
   [counter]
   [button]])

(defn content []
  [:div.row
   [:div.col-xs-6.col-md-4 [toolbox]]
   [:div.col-xs-6.col-md-4 [constructor]]])

(defn navbar []
  [:nav.navbar.navbar-default
   [:div.navbar-header
    [:span.navbar-brand
     "Exsto - web form constructor"]]])

(defn main-panel []
  [:div.container
   [navbar]
   [content]])
