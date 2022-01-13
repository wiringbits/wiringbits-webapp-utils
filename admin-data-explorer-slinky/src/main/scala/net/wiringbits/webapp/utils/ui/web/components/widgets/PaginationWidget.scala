package net.wiringbits.webapp.utils.ui.web.components.widgets

import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.react.mod.ChangeEvent
import org.scalajs.dom.{HTMLButtonElement, HTMLInputElement, HTMLTextAreaElement}
import slinky.web.SyntheticMouseEvent

import scala.scalajs.js.{Array => JSArray}
import scala.scalajs.js.|

@react object PaginationWidget {

  case class Props(
      numberOfRows: Int,
      currentPage: Int,
      onPageChange: (Null | SyntheticMouseEvent[HTMLButtonElement], Double) => Unit,
      onChangeRowsPerPage: ChangeEvent[HTMLTextAreaElement | HTMLInputElement] => Unit,
      pageLimit: Int
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val pageOptions: JSArray[Double] = JSArray(5.0, 10.0, 25.0)

    mui
      .TablePagination(
        count = props.numberOfRows,
        onChangePage = props.onPageChange,
        page = props.currentPage,
        rowsPerPage = props.pageLimit
      )
      .onChangeRowsPerPage(props.onChangeRowsPerPage(_))
      .rowsPerPageOptions(pageOptions)
  }
}
