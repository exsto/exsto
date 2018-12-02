(ns exsto.views
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [exsto.subs :as subs]))

(defn counter []
  (let [count (rf/subscribe [::subs/count])]
    [:p [:strong "number of forms: "] @count]))

(defn button []
  [:button.btn.btn-primary
   {:type "button"
    :on-click #(rf/dispatch [:button-click])}
   "Add dummy item"])

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
  (get form-components (:type form)))

(defn mousedown [e]
  (.preventDefault e)
  (let [ox (-> e .-nativeEvent .-offsetX)
        oy (-> e .-nativeEvent .-offsetY)]
    (rf/dispatch [:grab-target ox oy])))

(defn create-target [target-type]
  (fn [e]
    (.preventDefault e)
    (let [x (-> e .-clientX)
          y (-> e .-clientY)
          ox (-> e .-nativeEvent .-offsetX)
          oy (-> e .-nativeEvent .-offsetY)]
      (rf/dispatch [:create-target ox oy x y target-type])
      (rf/dispatch [:grab-target ox oy]))))

(defn mousemove [e]
  (.preventDefault e)
  (let [x    (-> e .-clientX)
        y    (-> e .-clientY)
        canmove (:can-move? @(rf/subscribe [::subs/draggable]))]
    (if canmove (rf/dispatch [:move-target x y]))))

(defn mouseup [e] (.preventDefault e) (rf/dispatch [:release-target]))

(defn drop-target [e]
  (.preventDefault e)
  (rf/dispatch [:drop-target])
  (rf/dispatch [:release-target]))

(defn draggable [pos]
  {:on-mouse-down mousedown
   :on-mouse-up   mouseup
   :style {:background "magenta"
           :border-width "5px"
           :border-color "magenta"
           :border-style "solid"
           :position "absolute"
           :left (str (:x pos) "px")
           :top  (str (:y pos) "px")
           :z-index 1}})

(defn toolbox []
  (let [input-val (r/atom "input value")
        cb        (r/atom true)]
    (fn []
      [:ul
       [:li
        {:on-mouse-down (create-target :drag-input)}
        [drag-input input-val]]
       [:li
        {:on-mouse-down (create-target :drag-button)}
        [drag-button]]
       [:li
        {:on-mouse-down (create-target :drag-checkbox)}
        [drag-checkbox cb]]])))

(defn dropzone [e]
  (let [inputs (rf/subscribe [::subs/form-inputs])]
    (fn []
      ;; (.log js/console (str @inputs))
      [:form.dropzone
       {:on-mouse-up drop-target}
       (for [form @inputs]
         ^{:key (str "item-" (:id form))}
         [:div.form-group
          {:data-form-id (str (:id form))}
          (resolve-form form)])])))

(defn drag-item []
  (let [item (rf/subscribe [::subs/draggable])]
    (fn []
      (if (:created? @item)
        [:li (draggable (:pos @item))
             (resolve-form @item)]))))

(defn constructor []
  [:div
   [dropzone]
   [counter]
   [button]])

(defn content []
  [:div.row
   [drag-item]
   [:div.col-xs-6.col-md-4 [toolbox]]
   [:div.col-xs-6.col-md-4 [constructor]]])

(defn navbar []
  [:nav.navbar.navbar-default
   [:div.navbar-header
    [:span.navbar-brand
     "Exsto - web form constructor"]]])

(defn main-panel []
  [:div.container {:on-mouse-up   mouseup
                   :on-mouse-move mousemove}
   [navbar]
   [content]])
