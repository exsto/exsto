(ns exsto.db)

(def default-db {:form-inputs []
                 :draggable {:can-move? false
                             :created? false
                             :type nil
                             :pos {:x 0  :y 0
                                   :ox 0 :oy 0}}})
