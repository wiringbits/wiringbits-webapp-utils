package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.react.mod.ChangeEvent
import org.scalajs.dom.{HTMLButtonElement, HTMLInputElement, HTMLTextAreaElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.SyntheticMouseEvent

import scala.scalajs.js.{|, Array => JSArray}

object PaginationWidget {

  case class Props(
      numberOfRows: Int,
      currentPage: Int,
      onPageChange: (Null | SyntheticMouseEvent[HTMLButtonElement], Double) => Unit,
      onChangeRowsPerPage: ChangeEvent[HTMLTextAreaElement | HTMLInputElement] => Unit,
      pageLimit: Int
  )

  def apply(
      numberOfRows: Int,
      currentPage: Int,
      onPageChange: (Null | SyntheticMouseEvent[HTMLButtonElement], Double) => Unit,
      onChangeRowsPerPage: ChangeEvent[HTMLTextAreaElement | HTMLInputElement] => Unit,
      pageLimit: Int
  ): KeyAddingStage = {
    component(
      Props(
        numberOfRows = numberOfRows,
        currentPage = currentPage,
        onPageChange = onPageChange,
        onChangeRowsPerPage = onChangeRowsPerPage,
        pageLimit = pageLimit
      )
    )
  }

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
